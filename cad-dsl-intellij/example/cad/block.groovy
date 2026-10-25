// Run it from the icon of the first line: the model shows in the CAD Preview, and saving the file runs it again
double side = param('side', 20)

cd().box(side, side, side / 2).cut {
    position(new Vec(side / 2, side / 2, 0)) {
        cylinder(side / 4, side)
    }
}.display()
