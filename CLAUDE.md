# cad-builder

A Groovy DSL for CAD (`cad-dsl`) on OCCT 8 through a C API (`cad-dsl-native`, bound with jextract in `cad-dsl-occt`),
with a FreeCAD kernel (`cad-dsl-freecad`), a STEP to DSL converter (`cad-dsl-step`), a runner for the IDE
(`cad-dsl-runner`) and an IntelliJ plugin with a CAD preview (`cad-dsl-intellij`). The user writes in French; the code,
the comments and the docs are in English.

Writing models: see `cad-dsl-intellij/example/CLAUDE.md`. The DSL and the C API are delivered as binaries to those who
write models: change them only when the user asks.

## Build and run

- OCCT is found through `occtHome` in `~/.gradle/gradle.properties`.
- Build the native library and the CLI: `./gradlew -q :cad-dsl-native:linkRelease :cad-dsl-occt:installDist`. When
  `cad-dsl-occt/build` or the native library is missing, or a script behaves as with older code, rebuild them first.
- Run a script (the classes beside it are found):
  ```
  CAD_DSL_OPTS="-Djava.library.path=$PWD/cad-dsl-native/build/lib/main/release" \
    cad-dsl-occt/build/install/cad-dsl/bin/cad-dsl path/to/Main.groovy args...
  ```
- `-Dcad.trace=true` in `CAD_DSL_OPTS` prints every call of the OCCT visitor and the C API, off by default.
- Tests: `./gradlew :cad-dsl:test :cad-dsl-occt:test`. The DSL tests are fixtures (`cad-dsl/src/testFixtures`) that each
  kernel runs; FreeCAD excludes the tags `images`, `gltf` and `measures`. Tests use `display(image())`, never
  `display()`, which opens a window.
- IntelliJ plugin: `./gradlew :cad-dsl-intellij:runIde` starts an IDE for the user to try a change.

## Docs and git

- Docs are AsciiDoc in `docs/`. When a feature of the DSL, the runner or the plugin changes, update `dsl.adoc` or
  `usage.adoc`; the images come from the tests.
- Commit and push only when asked, and check the branch first. Commit subjects are short, lowercase, without a prefix.
