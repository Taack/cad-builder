package org.taack.cad.intellij;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Runs scripts in cad-dsl-runner, built by ./gradlew :cad-dsl-runner:installDist with the native library of the project
 */
public class CadRunnerIntegrationTest extends BasePlatformTestCase {
    private static final long RUN_TIMEOUT_SECONDS = 120;
    private static final byte[] GLB_MAGIC = "glTF".getBytes(StandardCharsets.US_ASCII);
    private Path scripts;
    private CadSettings.State saved;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        scripts = Files.createTempDirectory("cad-scripts");
        CadSettings.State settings = CadSettings.getInstance().getState();
        saved = settings;
        CadSettings.State runner = new CadSettings.State();
        runner.runnerHome = System.getProperty("cad.runner.home");
        runner.nativeLibraryDir = System.getProperty("cad.native.dir");
        CadSettings.getInstance().loadState(runner);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            CadRunner.getInstance(getProject()).stop();
            CadSettings.getInstance().loadState(saved);
        } finally {
            super.tearDown();
        }
    }

    public void testARunReturnsItsModelsAndParams() throws Exception {
        CadRunResult result = run(script("block.groovy", "cd().box(param('length', 10), 2, 3).display()\ncd().cylinder(1, 2).display()"));
        assertNull(result.trace(), result.error());
        assertEquals(2, result.models().size());
        assertTrue(Arrays.equals(GLB_MAGIC, Arrays.copyOf(result.models().getFirst().glb(), GLB_MAGIC.length)));
        assertEquals("10", result.params().get("length").replaceFirst("\\.0$", ""));
    }

    public void testARunReturnsTheFeaturesOfItsScript() throws Exception {
        CadRunResult result = run(script("cut.groovy", "cd().box(10, 10, 5).cut {\n    cylinder(2, 10)\n}.display()"));
        assertNull(result.trace(), result.error());
        CadFeature cut = result.features().getFirst().children().get(1);
        assertEquals("cut", cut.label());
        CadFeature cylinder = cut.children().getFirst();
        assertEquals(List.of("cylinder r2 × 10", "cut.groovy", 2), List.of(cylinder.label(), cylinder.file(), cylinder.line()));
    }

    public void testARollBackRunsTheScriptUpToAFeatureUntilRolledForward() throws Exception {
        CadPreview preview = CadPreview.getInstance(getProject());
        CadRunRequest request = new CadRunRequest(script("cut.groovy", "cd().box(10, 10, 5).cut {\n    cylinder(2, 10)\n}.display()").toString(), List.of());
        preview.show(run(request));

        CadRunResult stopped = shownAfter(() -> preview.rollBack("solid0/box0"));
        assertEquals("solid0/box0", stopped.stopAt());
        List<CadFeature> features = stopped.features().getFirst().children();
        assertEquals(List.of(false, true), List.of(features.get(0).suppressed(), features.get(1).suppressed()));
        // the next runs of the script stop there too
        assertEquals("solid0/box0", run(request).stopAt());

        CadRunResult forward = shownAfter(() -> preview.rollBack(null));
        assertNull(forward.stopAt());
        assertNull(preview.stopAt(request.script()));
    }

    // The result CadPreview shows after action
    private CadRunResult shownAfter(Runnable action) throws Exception {
        CompletableFuture<CadRunResult> shown = new CompletableFuture<>();
        Disposable listening = Disposer.newDisposable();
        try {
            CadPreview.getInstance(getProject()).addListener(new CadPreview.Listener() {
                @Override
                public void running(@NotNull CadRunRequest request) {}

                @Override
                public void shown(@NotNull CadRunResult result) {
                    shown.complete(result);
                }
            }, listening);
            action.run();
            return shown.get(RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } finally {
            Disposer.dispose(listening);
        }
    }

    public void testTheArgumentsReachTheScript() throws Exception {
        CadRunResult result = run(new CadRunRequest(script("arguments.groovy", "assert args.toList() == ['a', 'b c']").toString(), List.of("a", "b c")));
        assertTrue(result.error(), result.succeeded());
    }

    public void testAFailingScriptIsReportedAndTheRunnerGoesOn() throws Exception {
        CadRunResult failed = run(script("broken.groovy", "throw new IllegalStateException('no rail')"));
        assertEquals("no rail", failed.error());
        assertTrue(failed.trace().contains("broken.groovy"));
        assertTrue(run(script("fine.groovy", "cd().box(1, 2, 3).display()")).succeeded());
    }

    public void testWhatTheScriptPrintsIsForwarded() throws Exception {
        CountDownLatch printed = new CountDownLatch(1);
        CompletableFuture<CadRunResult> end = new CompletableFuture<>();
        CadRunner.getInstance(getProject()).submit(new CadRunRequest(script("hello.groovy", "println 'hello rail'").toString(), List.of()), new CadRunListener() {
            @Override
            public void output(@NotNull String text, @NotNull Key<?> outputType) {
                if (text.contains("hello rail")) printed.countDown();
            }

            @Override
            public void finished(@NotNull CadRunResult result) {
                end.complete(result);
            }
        });
        assertTrue(end.get(RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS).succeeded());
        assertTrue("The output did not arrive", printed.await(RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    public void testARerunReplacedByALaterRunIsSkipped() throws Exception {
        Path script = script("block.groovy", "cd().box(1, 2, 3).display()");
        CadRunRequest request = new CadRunRequest(script.toString(), List.of());
        CompletableFuture<Boolean> skipped = new CompletableFuture<>();
        // keeps the runner busy while the two runs of block.groovy are submitted
        CadRunner.getInstance(getProject()).submit(
                new CadRunRequest(script("slow.groovy", "Thread.sleep(1000)").toString(), List.of()), result -> {});
        CadRunner.getInstance(getProject()).submitUnlessSuperseded(request, new CadRunListener() {
            @Override
            public void skipped() {
                skipped.complete(true);
            }

            @Override
            public void finished(@NotNull CadRunResult result) {
                skipped.complete(false);
            }
        });
        assertTrue(run(request).succeeded());
        assertTrue(skipped.get(RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    public void testARerunReplacedByARunOfAnotherScriptIsSkipped() throws Exception {
        CadRunRequest request = new CadRunRequest(script("block.groovy", "cd().box(1, 2, 3).display()").toString(), List.of());
        CompletableFuture<Boolean> skipped = new CompletableFuture<>();
        CadRunner.getInstance(getProject()).submit(
                new CadRunRequest(script("slow.groovy", "Thread.sleep(1000)").toString(), List.of()), result -> {});
        CadRunner.getInstance(getProject()).submitUnlessSuperseded(request, new CadRunListener() {
            @Override
            public void skipped() {
                skipped.complete(true);
            }

            @Override
            public void finished(@NotNull CadRunResult result) {
                skipped.complete(false);
            }
        });
        assertTrue(run(script("other.groovy", "cd().cylinder(1, 2).display()")).succeeded());
        assertTrue(skipped.get(RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    public void testStoppingEndsTheRunAndTheNextRunStartsTheRunnerAgain() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CompletableFuture<CadRunResult> end = new CompletableFuture<>();
        CadRunner runner = CadRunner.getInstance(getProject());
        // the first run starts the runner: the loop only runs once it is up
        assertTrue(run(script("warm.groovy", "cd()")).succeeded());
        runner.submit(new CadRunRequest(script("loop.groovy", "while (true) Thread.sleep(10)").toString(), List.of()), new CadRunListener() {
            @Override
            public void started() {
                started.countDown();
            }

            @Override
            public void finished(@NotNull CadRunResult result) {
                end.complete(result);
            }
        });
        assertTrue(started.await(RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Thread.sleep(500);
        runner.stop();
        assertEquals("Stopped", end.get(RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS).error());
        assertTrue(run(script("again.groovy", "cd().box(1, 2, 3).display()")).succeeded());
    }

    private Path script(String name, String text) throws IOException {
        return Files.writeString(scripts.resolve(name), text);
    }

    private CadRunResult run(Path script) throws Exception {
        return run(new CadRunRequest(script.toString(), List.of()));
    }

    private CadRunResult run(CadRunRequest request) throws Exception {
        CompletableFuture<CadRunResult> end = new CompletableFuture<>();
        CadRunner.getInstance(getProject()).submit(request, end::complete);
        return end.get(RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
