/*
 * Pillow block with counterbored holes.
 *
 *   cad-dsl samples/pillow-block.groovy               opens the viewer
 *   cad-dsl samples/pillow-block.groovy block.step    exports (.step, .stl, or an image)
 *
 * The imports are optional with cad-dsl, they give code completion in the IDE.
 */
import static org.taack.cad.dsl.CadDsl.cd

double length = 80.0
double height = 60.0
double thickness = 10.0
double centerHoleDia = 4.0
double cboreHoleDiameter = 2.4
double cboreInset = 12.0

cd().box(length, height, thickness).topZ().wireFrom() {
    double rectSX = length - cboreInset
    double rectSY = height - cboreInset
    [[-rectSX / 2, -rectSY / 2], [0, rectSY],
     [rectSX, 0], [0, -rectSY]].each { double cX, double cY ->
        move(cX, cY)
        circle(centerHoleDia)
    }
}.toFace().hole(-cboreHoleDiameter).topZ().wireFrom() {
    circle(centerHoleDia)
}.toFace().prism(cboreHoleDiameter).display(args ? args[0] : null)
