DSL for CAD Bodies and Assemblies

```groovy
    @Test
    void "Pillow Block With Counterbored Holes"() {
        cb().box(length, height, thickness).topZ().rect(length - cboreInset, height - cboreInset) {
            counterboredHole(cboreHoleDiameter, cboreDiameter, cboreDepth)
        }.display("test3.png", 640, 480)
    }

```
