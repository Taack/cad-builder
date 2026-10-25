package org.taack.cad.intellij;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Runs the CAD scripts of a project in a cad-dsl-runner process, one at a time, started at the first run and kept up.
 * The protocol is described in org.taack.cad.runner.CadRunner of cad-dsl-runner.
 */
@Service(Service.Level.PROJECT)
public final class CadRunner implements Disposable {
    static final int PROTOCOL = 1;
    // the runner JVM loads Groovy, the DSL and the kernel before it connects
    private static final long CONNECT_TIMEOUT_MILLIS = 60_000;
    private static final int ACCEPT_POLL_MILLIS = 200;
    private static final String RUNNER_NAME = "cad-dsl-runner";
    private static final Logger LOG = Logger.getInstance(CadRunner.class);

    private final Project project;
    private final ExecutorService queue = AppExecutorUtil.createBoundedApplicationPoolExecutor("CAD DSL runner", 1);
    private int lastId = 0;
    private final AtomicLong submissions = new AtomicLong();
    private final Map<String, Long> lastSubmissions = new ConcurrentHashMap<>();
    // the output of a run reaches the IDE after its end event at times: it goes to the last run until the next one
    private volatile @Nullable CadRunListener current;
    private volatile boolean stopping;
    private volatile @Nullable KillableProcessHandler process;
    private @Nullable Socket socket;
    private BufferedReader events;
    private Writer requests;

    public CadRunner(Project project) {
        this.project = project;
    }

    public static CadRunner getInstance(@NotNull Project project) {
        return project.getService(CadRunner.class);
    }

    /**
     * Runs request after the runs submitted before it
     */
    public Future<?> submit(@NotNull CadRunRequest request, @NotNull CadRunListener listener) {
        recordSubmission(request);
        return queue.submit(() -> execute(request, listener));
    }

    /**
     * Runs request unless another run of the same script is submitted before it starts
     */
    public void submitUnlessSuperseded(@NotNull CadRunRequest request, @NotNull CadRunListener listener) {
        long submission = recordSubmission(request);
        queue.submit(() -> {
            if (lastSubmissions.get(request.script()) == submission) execute(request, listener);
            else listener.skipped();
        });
    }

    private long recordSubmission(CadRunRequest request) {
        long submission = submissions.incrementAndGet();
        lastSubmissions.merge(request.script(), submission, Math::max);
        return submission;
    }

    /**
     * Stops the runner, and the script it runs: a script cannot be interrupted otherwise. The next run starts it again.
     */
    public void stop() {
        KillableProcessHandler running = process;
        if (running == null) return;
        stopping = true;
        running.killProcess();
    }

    private void execute(CadRunRequest request, CadRunListener listener) {
        long start = System.nanoTime();
        current = listener;
        stopping = false;
        listener.started();
        try {
            ensureStarted();
            int id = ++lastId;
            send(runRequest(request, id));
            listener.finished(awaitEnd(request, id, start));
        } catch (IOException | ExecutionException | RuntimeException e) {
            String message = stopping ? "Stopped" : e.getMessage();
            shutdown();
            listener.finished(CadRunResult.failed(request, message, millisSince(start)));
        }
    }

    private CadRunResult awaitEnd(CadRunRequest request, int id, long start) throws IOException {
        List<CadModel> models = new ArrayList<>();
        for (String line = events.readLine(); line != null; line = events.readLine()) {
            JsonObject event = JsonParser.parseString(line).getAsJsonObject();
            if (!event.has("id") || event.get("id").getAsInt() != id) continue;
            switch (event.get("type").getAsString()) {
                case "display" -> {
                    Path glb = Path.of(event.get("file").getAsString());
                    JsonElement feature = event.get("feature");
                    models.add(new CadModel(glb.getFileName().toString(), Files.readAllBytes(glb),
                            feature == null || feature.isJsonNull() ? null : feature.getAsString()));
                }
                case "done" -> {
                    return new CadRunResult(request, models, params(event), features(event), millis(event, start), null, null);
                }
                case "error" -> {
                    return new CadRunResult(request, models, params(event), features(event), millis(event, start),
                            event.get("message").getAsString(), event.has("trace") ? event.get("trace").getAsString() : null);
                }
                default -> LOG.warn("Unknown runner event: " + line);
            }
        }
        throw new IOException("The runner stopped" + exitCode());
    }

    /**
     * Writes the shapes of a feature of the last run, after the runs submitted before
     *
     * @return The binary glTF of its shapes, in the coordinates of its part; null when it has none, or when the runner is
     * not up: the features are those of its last run
     */
    public CompletableFuture<byte @Nullable []> shape(@NotNull String feature) {
        CompletableFuture<byte @Nullable []> shape = new CompletableFuture<>();
        queue.submit(() -> {
            try {
                shape.complete(readShape(feature));
            } catch (IOException | RuntimeException e) {
                // the next run starts the runner again
                LOG.info("No shape for " + feature, e);
                shutdown();
                shape.complete(null);
            }
        });
        return shape;
    }

    private byte @Nullable [] readShape(String feature) throws IOException {
        KillableProcessHandler running = process;
        if (running == null || running.isProcessTerminated() || socket == null) return null;
        int id = ++lastId;
        JsonObject request = new JsonObject();
        request.addProperty("type", "shape");
        request.addProperty("id", id);
        request.addProperty("feature", feature);
        send(request);
        for (String line = events.readLine(); line != null; line = events.readLine()) {
            JsonObject event = JsonParser.parseString(line).getAsJsonObject();
            if (!event.has("id") || event.get("id").getAsInt() != id) continue;
            if (event.has("file")) return Files.readAllBytes(Path.of(event.get("file").getAsString()));
            if (event.has("message")) LOG.info("No shape for " + feature + ": " + event.get("message").getAsString());
            return null;
        }
        throw new IOException("The runner stopped" + exitCode());
    }

    private void ensureStarted() throws IOException, ExecutionException {
        KillableProcessHandler running = process;
        if (running != null && !running.isProcessTerminated()) return;
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            server.setSoTimeout(ACCEPT_POLL_MILLIS);
            KillableProcessHandler started = new KillableProcessHandler(commandLine(server.getLocalPort()));
            started.addProcessListener(new ProcessListener() {
                @Override
                public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                    CadRunListener listener = current;
                    if (listener != null && outputType != ProcessOutputTypes.SYSTEM) listener.output(event.getText(), outputType);
                }
            });
            process = started;
            started.startNotify();
            socket = accept(server, started);
        }
        events = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        requests = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        String line = events.readLine();
        JsonObject ready = line == null ? null : JsonParser.parseString(line).getAsJsonObject();
        if (ready == null || !"ready".equals(ready.get("type").getAsString()) || ready.get("protocol").getAsInt() != PROTOCOL) {
            throw new IOException("The runner does not speak protocol " + PROTOCOL + ": " + line);
        }
    }

    private Socket accept(ServerSocket server, KillableProcessHandler started) throws IOException {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(CONNECT_TIMEOUT_MILLIS);
        while (true) {
            try {
                return server.accept();
            } catch (SocketTimeoutException e) {
                if (started.isProcessTerminated()) throw new IOException("The runner stopped while starting" + exitCode());
                if (System.nanoTime() > deadline) {
                    started.killProcess();
                    throw new IOException("The runner did not connect within " + CONNECT_TIMEOUT_MILLIS / 1000 + " s");
                }
            }
        }
    }

    private GeneralCommandLine commandLine(int port) throws IOException {
        CadSettings.State settings = CadSettings.getInstance().getState();
        Path home = settings.runnerHome.isBlank() ? inProject("cad-dsl-runner/build/install/" + RUNNER_NAME) : Path.of(settings.runnerHome);
        Path launcher = home.resolve("bin").resolve(SystemInfo.isWindows ? RUNNER_NAME + ".bat" : RUNNER_NAME);
        if (!Files.isRegularFile(launcher)) {
            throw new IOException("No CAD runner at " + launcher + ": build it with ./gradlew :cad-dsl-runner:installDist, "
                    + "or set its installation in Settings | Tools | CAD DSL");
        }
        Path natives = settings.nativeLibraryDir.isBlank() ? inProject("cad-dsl-native/build/lib/main/release") : Path.of(settings.nativeLibraryDir);
        // the runtime of the IDE is a Java 25 since IntelliJ 2026.2, as the runner needs
        String javaHome = settings.javaHome.isBlank() ? System.getProperty("java.home") : settings.javaHome;
        GeneralCommandLine command = new GeneralCommandLine(launcher.toString(), String.valueOf(port))
                .withCharset(StandardCharsets.UTF_8)
                .withEnvironment("JAVA_HOME", javaHome)
                .withEnvironment("CAD_DSL_RUNNER_OPTS", "-Djava.library.path=" + natives);
        if (project.getBasePath() != null && Files.isDirectory(Path.of(project.getBasePath()))) {
            command.withWorkingDirectory(Path.of(project.getBasePath()));
        }
        return command;
    }

    private Path inProject(String path) {
        return Path.of(project.getBasePath() == null ? "" : project.getBasePath()).resolve(path);
    }

    private void send(JsonObject request) throws IOException {
        requests.write(request.toString());
        requests.write('\n');
        requests.flush();
    }

    private static JsonObject runRequest(CadRunRequest request, int id) {
        JsonObject run = new JsonObject();
        run.addProperty("type", "run");
        run.addProperty("id", id);
        run.addProperty("script", request.script());
        JsonArray args = new JsonArray();
        request.args().forEach(args::add);
        run.add("args", args);
        JsonArray classpath = new JsonArray();
        request.classpath().forEach(classpath::add);
        run.add("classpath", classpath);
        return run;
    }

    private static Map<String, String> params(JsonObject event) {
        Map<String, String> params = new LinkedHashMap<>();
        if (event.has("params")) {
            for (Map.Entry<String, JsonElement> param : event.getAsJsonObject("params").entrySet()) {
                JsonElement value = param.getValue();
                params.put(param.getKey(), value.isJsonPrimitive() ? value.getAsString() : value.toString());
            }
        }
        return params;
    }

    private static List<CadFeature> features(JsonObject event) {
        return CadFeature.read(event.has("features") ? event.getAsJsonArray("features") : null);
    }

    private static long millis(JsonObject event, long start) {
        return event.has("millis") ? event.get("millis").getAsLong() : millisSince(start);
    }

    private static long millisSince(long start) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }

    private String exitCode() {
        KillableProcessHandler stopped = process;
        Integer code = stopped == null ? null : stopped.getExitCode();
        return code == null ? "" : ", exit code " + code;
    }

    private synchronized void shutdown() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            LOG.debug(e);
        }
        socket = null;
        KillableProcessHandler running = process;
        if (running != null && !running.isProcessTerminated()) running.killProcess();
    }

    @Override
    public void dispose() {
        queue.shutdownNow();
        shutdown();
    }
}
