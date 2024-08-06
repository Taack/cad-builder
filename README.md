= DSL for CAD Bodies and Assemblies

Get inspiratoin from CAD Query, but using Groovy DSL.

```groovy
    @Test
    void "Pillow Block With Counterbored Holes"() {
        cb().box(length, height, thickness).topZ().rect(length - cboreInset, height - cboreInset) {
            counterboredHole(cboreHoleDiameter, cboreDiameter, cboreDepth)
        }.display("test3.png", 640, 480)
    }

```

![test](https://github.com/Taack/cad-builder/blob/main/test3.png?raw=true)
