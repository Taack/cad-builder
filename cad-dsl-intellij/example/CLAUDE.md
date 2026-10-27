# CAD models

Models written with `cad-dsl`, a Groovy DSL for CAD on OCCT. The DSL comes as a jar and a native library: use it as it
is. When it lacks something or behaves wrongly, say so, with the smallest script showing it, rather than working around
it silently. The user writes in French; the code and its comments are in English.

Read the "How a Model is Built" and "Checking a Model" sections of the DSL documentation (`docs/dsl.adoc` of
cad-builder) first. Examples in the expected style: `cad/Stepper17HS24.groovy`, `cad/EdfJet.groovy`,
`cad/MesserschmittP1111.groovy`, `cad/ProjecteurObstaflashFaa.groovy`.

## Writing a model

- One class with `@CompileStatic`, one public static method per part, and a `...Main.groovy` script beside it that
  displays the assembly, or writes the files with a directory as argument.
- Double literals (`2d`), named `static final` constants for any value or `Vec` used more than once, typed data rather
  than lists of lists, `param()` for the dimensions the user may tune. English names, meaningful ones, also for the
  parts of the assembly, which has colors.
- Concise and readable: comments say why, not what.
- State the dimensions taken from data sheets, which the user should measure.

## Checking a model

Check in the script rather than with scripts beside it, in a `verify()` method of the class:

- `check(name)` on each part, `measure()` for masses and centers of gravity, `overlap()` for the fit of what goes in.
- `checkContours { ... }` on the assembly, above all for a model converted from STEP, and act on each finding. The
  largest gains in size and readability come from merging close contours: one method with parameters for nearly the
  same contours, one constant for positions shared by several parts, a loop for copies and repeated elements, and
  `text()` for the letters drawn as contours.
- After a simplification, compare the volumes of the parts with those before it.

## Running

- `cad-dsl Main.groovy [args]` runs a script, the classes beside it are found. `-Dcad.trace=true` in `CAD_DSL_OPTS`
  prints what the kernel does.
- `display("x.png")` writes PPM data whatever the extension: `magick x.png x2.png` before reading it.
