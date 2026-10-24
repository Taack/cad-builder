/*
 * Pillow block whose dimensions are parameters: with the FreeCAD kernel, block.FCStd has them in its Params
 * spreadsheet, the features bound to them.
 *
 *   cad-dsl-freecad samples/pillow-block-params.groovy               opens FreeCAD
 *   cad-dsl-freecad samples/pillow-block-params.groovy block.FCStd   saves the editable document
 *
 * The imports are optional with cad-dsl, they give code completion in the IDE.
 */
import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.cad.dsl.CadDsl.param

double length = param('length', 80)
double thickness = param('thickness', 10)
double holeRadius = param('holeRadius', 4)

cd().box(length, 60, thickness).topZ().wireFrom() {
    circle(holeRadius)
}.toFace().hole(-thickness).display(args ? args[0] : null)
