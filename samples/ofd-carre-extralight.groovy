/*
 * Obsta obstruction light OFD carré extralight (OFD+carré+extralight+(2).STEP): a square housing on four feet, a
 * flange, the cylindrical globe of the light between two dark discs, a stepped cover with six spokes and the lens of
 * its photocell, the screws of the cover, two cable glands, a vent and an M12 connector in the recess of the front
 * (+z), and the label of a side (+x).
 *
 * The STEP file is an "extralight" model: the surfaces that can be seen only, without volumes. They are made solids
 * here: the housing is full under its floor (y = 5), the globe a tube 2 thick, the discs 1 thick. The roofs of the
 * pockets under the floor, missing, are put where their R2 fillets start. The parts placed several times are written
 * once: the discs, the spokes, the logos of the cover, the screws of the cover and the pins of the connector.
 *
 * The walls of the molded parts lean 2° (prisms with a draft); the fillets made after the drafts, of a constant radius,
 * are drawn at mid-height of their walls. The texts and the logos come from the STEP file, in the SVG files beside
 * this script.
 *
 * Left out: the R1 and R2 fillets of the housing (mouths of the pockets, ledges of the feet, recess) and of the slopes
 * of the spokes, the chamfers of the bottom of the recess, the threads and the markings of the connector and of the
 * cable glands.
 * The colors are those of the parts: the STEP file colors a few faces apart (holes of the feet, contact faces), and
 * a few characters of the label like the label itself, flush with it.
 *
 *   cad-dsl samples/ofd-carre-extralight.groovy               opens the viewer
 *   cad-dsl samples/ofd-carre-extralight.groovy ofd.step      exports (.step, .stl, or an image)
 *
 * The imports are optional with cad-dsl, they give code completion in the IDE.
 */
import org.taack.cad.dsl.CadDsl
import org.taack.cad.dsl.CadDslAssembly
import org.taack.cad.dsl.Color
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static java.lang.Math.*
import static org.taack.cad.dsl.CadDsl.assembly

// Files beside this script
File here = new File(getClass().protectionDomain.codeSource.location.toURI()).parentFile

// Colors of the STEP file
Color white = Color.WHITE
Color cream = Color.rgb(255, 242, 232)      // washers
Color steel = Color.rgb(198, 193, 188)      // cable glands, vent, connector, nuts
Color grey = Color.rgb(175, 175, 175)       // caps of the screws
Color dark = Color.rgb(26, 26, 26)          // discs
Color lensBlue = Color.rgb(247, 255, 255)
Color labelBlue = Color.rgb(221, 232, 255)

// Y up, the feet at y = 0. The walls of the molded parts lean 2°.
double t = tan(toRadians(2)), s2 = sin(toRadians(2)), c2 = cos(toRadians(2))
// The walls of the housing are drawn on the plane y = datum: how far inward a drawing is found on the plane y
double datum = 100.003
Closure<Double> inset = { double y -> (y - datum) * t }
// Planes of the drawings, (u, v) = (x, z) on the plane y, (r, y) for the sections of solids of revolution
Closure<Closure<Vec>> atY = { double y -> Contour.xz(y) }
Closure<Vec> section = Contour.xy(0)
// Revolution of a section around the y axis, or the z axis for the parts of the recess, drawn along z
Closure revolveY = { CadDsl dsl, Contour c -> c.face(dsl, section).revolution(new Vec(), Vec.vY) }
Closure revolveZ = { CadDsl dsl, Contour c -> c.face(dsl, Contour.xz(0)).revolution(new Vec(), Vec.vZ) }

// Recess of the front: its back leans 5°, its sides (x = ±50 under its ceiling) 2°
double recessCeiling = 92.856
Closure<Double> recessZ = { double y -> 118.1258 + y * tan(toRadians(5)) }
Closure<Double> recessX = { double y -> 50 + (recessCeiling - y) * t }
// Parts of the recess: their axis, normal to its back, and the centers of their bases
Vec recessAxis = new Vec(0, -sin(toRadians(5)), cos(toRadians(5)))
Closure<Vec> onRecess = { double x, double y -> new Vec(x, y, recessZ(y)) }
Vec bigGland = onRecess(-25, 65.4505), smallGland = onRecess(25, 65.4505)
Vec vent = onRecess(-25, 25.6025), connector = onRecess(25, 25.6025)

assembly('OFD carré extralight') {

    // Housing: a square body with corners rounded R80, notched over the feet for the screws (R20 fillets at mid-height),
    // a skirt around the feet, pockets under the floor, and the recess of the front
    def housing = part('housing', white) {
        CadDsl housing = delegate as CadDsl
        double floorY = 5, ledgeY = 10.003, bodyTopY = 94.503
        new Outline()
                .circle(54, 54, 80).fillet(20).circle(119, 119, -16.692).fillet(20).circle(54, 54, 80)
                .quarterTurns()
                .contour(inset(ledgeY), inset((ledgeY + bodyTopY) / 2)).face(delegate, atY(ledgeY))
                .prism(new Vec(0, bodyTopY - ledgeY, 0), draft: 2)

        // skirt, around the feet: they widen 2° upward
        Outline feet = new Outline().circle(119, 119, 18.318).quarterTurns()
        feet.contour().face(delegate, atY(floorY)).prism(new Vec(0, ledgeY - floorY, 0), draft: -2)

        // the four sides, on the planes x, z = ±134 of the datum plane
        Closure<Contour> square = { double y -> Contour.rectangle(2 * (134 - inset(y)), 2 * (134 - inset(y))) }
        Contour.rectangle(400, 400).face(delegate, atY(-1), [square(-1)]).hole(new Vec(0, bodyTopY + 2, 0), draft: -2)

        // Pockets, molded with the walls (R3 fillets at mid-height): along the sides, by the corners, around the
        // bosses of the corners. Their roofs are at y = 80, and 75 around the bosses.
        // Ribs between the pockets of a corner (+x, +z) and of its sides, along x and along z: 10 wide, 21.371° from
        // the axes, their walls at 25.609 and 35.609 from the origin
        double rib = toRadians(21.3711), ribIn = 25.609, ribOut = 35.609
        Closure<List<Double>> ribX = { double offset, double way -> [-sin(rib) * offset, cos(rib) * offset, way * cos(rib), way * sin(rib)] }
        Closure<List<Double>> ribZ = { double offset, double way -> [cos(rib) * offset, -sin(rib) * offset, way * sin(rib), way * cos(rib)] }

        Outline straight = new Outline()
                .line(130.993, 0, 0, 1).fillet(3).line(0, 44, -1, 0).fillet(3)
                .line(126, 0, 0, -1).fillet(3).line(0, 5, 1, 0).fillet(3)
        Outline bySide = new Outline()
                .circle(54, 54, 76.993).fillet(3).line(*ribX(ribIn, -1)).fillet(3)
                .circle(54, 54, -72).fillet(3).line(0, 54, 1, 0).fillet(3)
        // by the recess, the pockets stop at x = ±60
        Outline byRecess = new Outline()
                .circle(54, 54, 76.993).fillet(3).line(60, 0, 0, -1).fillet(3)
                .circle(54, 54, -72).fillet(3).line(*ribZ(ribIn, 1)).fillet(3)
        Outline byCorner = new Outline()
                .circle(54, 54, 76.993).fillet(3).circle(119, 119, -21.635).fillet(3).circle(54, 54, 76.993).fillet(3)
                .line(*ribZ(ribOut, -1)).fillet(3)
                .circle(75.5, 108.943, -13).circle(101, 101, 12.5).circle(108.943, 75.5, -13).fillet(3)
                .line(*ribX(ribOut, 1)).fillet(3)

        Closure pocket = { Outline o, double roofY ->
            o.contour(inset(floorY), inset((floorY + roofY) / 2)).face(housing, atY(floorY))
                    .hole(new Vec(0, roofY - floorY, 0), draft: 2)
        }
        // the sides +x, -x and -z, then the front, +z
        [0, 180, 270].each { int a ->
            [straight, straight.mirrorV(), bySide, bySide.mirrorV()].each { pocket(it.rotate(a), 80) }
        }
        [byRecess, byRecess.mirrorV().rotate(180)].each { pocket(it, 80) }
        [0, 90, 180, 270].each { int a -> pocket(byCorner.rotate(a), 75) }

        // a 1 mm chamfer at the bottom of the sides
        Contour.rectangle(400, 400).face(delegate, atY(6), [square(6)]).hole(new Vec(0, -2, 0), draft: -43)

        // feet, chamfered 47° under, their holes 45°
        List<List<Integer>> corners = [[119, 119], [-119, 119], [-119, -119], [119, -119]]
        fuse {
            corners.each { x, z ->
                direction(Vec.vY)
                position(new Vec(x, 0, z)) { cone(17.106, 18.318 - 4 * t, 1) }
                position(new Vec(x, 1, z)) { cone(18.318 - 4 * t, 18.318 + t, 5) }
            }
        }
        cut {
            corners.each { x, z ->
                direction(Vec.vY)
                position(new Vec(x, -0.001, z)) { cone(8.001, 7, 1.001) }
                position(new Vec(x, 1, z)) { cylinder(7, ledgeY) }
            }
        }

        // Recess of the front, then the bores of the cable glands, 8 deep beyond their bases
        wireFrom(onRecess(recessX(recessCeiling), recessCeiling)) {
            edge(onRecess(-recessX(recessCeiling), recessCeiling))
            edge(onRecess(-recessX(4), 4))
            edge(onRecess(recessX(4), 4))
            edge(onRecess(recessX(recessCeiling), recessCeiling))
        }.toFace().hole(new Vec(0, 0, 20))
        cut {
            [[bigGland, 4.5], [smallGland, 3.5]].each { Vec base, Number r ->
                direction(-recessAxis)
                position(base + recessAxis * 0.5) { cylinder(r, 8.5) }
            }
        }
    }
    place(housing)

    // Flange: the outline of the housing 0.5 higher, 9 thick, rounded R2 on top, under a ring holding the globe
    place(part('flange', white) {
        double bottomY = 95.003
        new Outline()
                .circle(54.192, 54.192, 80).fillet(20).circle(119, 119, -16.5).fillet(20).circle(54.192, 54.192, 80)
                .quarterTurns()
                .contour(0, 4.5 * t).face(delegate, atY(bottomY)).prism(new Vec(0, 9, 0), draft: 2)
        topY().fillet(2)
        // from the origin, not from the face of the fillet
        fuse {
            position(new Vec()) {
                revolveY(delegate, new Outline()
                        .line(0, 104.003, 1, 0).line(134.192, bottomY, -s2, c2).fillet(2)
                        .line(0, 110.003, -1, 0).line(127.5, 110.003, -1, -1)    // 0.5 chamfer of the bore
                        .line(127, 0, 0, -1).line(0, 104.503, -1, 0).line(0, 0, 0, -1)
                        .contour())
            }
        }
    })

    // Globe of the light, a tube between two dark discs: one closes the bore of the flange, the other the one of the cover
    place(part('globe', white) {
        direction(Vec.vY)
        position(new Vec(0, 107.992, 0)) { cylinder(125, 68) }
        cut {
            position(new Vec(0, 106, 0)) { cylinder(123, 72) }
        }
    })
    def disc = part('disc', dark) {
        direction(Vec.vY)
        position(new Vec(0, -1, 0)) { cylinder(127, 1) }
    }
    [108.503, 174.503].each { y -> place(disc) { translate(0, y, 0) } }

    // Cover: five terraces (2° slopes, R2 fillets on top of their 2° risers) around the hub of the lens, a rim
    // rounded R2, six spokes and two logos
    place(part('cover', white) {
        Outline cover = new Outline()
                .line(0, 178.503, 1, 0).line(127, 0, 0, -1)         // bore of the disc
                .line(127, 173.503, 1, -1)                          // 0.5 chamfer
                .line(0, 173.003, 1, 0).fillet(2)
                .line(131.5, 198.003, -s2, c2).fillet(2)            // rim
        // terraces, every 20.5 from the rim to the hub, their risers ending on the radius r
        (4..0).each { int k ->
            double r = 29 + 20.5 * k
            cover.line(r + 20.5, 180.003 + 3.446 * (4 - k), -c2, s2).line(r, 198.003, -s2, c2).fillet(2)
        }
        cover.line(0, 198.003, -1, 0).line(21, 0, 0, -1).line(0, 195.503, -1, 0)    // lens
                .line(0, 0, 0, -1)
        revolveY(delegate, cover.contour())
    })
    place(part('lens', lensBlue) {
        direction(Vec.vY)
        position(new Vec(0, 195.503, 0)) { cylinder(20.75, 2.5) }
    })

    // Spoke along +x: 16 wide on top, its sides leaning 2°, rounded R2, flat up to r = 65, then sloping 15.443° down
    // to the rim
    double slope = toRadians(15.443)
    def spoke = part('spoke', white) {
        new Outline()
                .line(0, 178.503, 1, 0).line(8, 198.003, -s2, c2).fillet(2)
                .line(0, 198.003, -1, 0).fillet(2).line(-8, 198.003, -s2, -c2)
                .contour().face(delegate, { Vec2d p -> new Vec(0, p.y, p.x) })
                .prism(new Vec(133, 0, 0))
        common {
            revolveY(delegate, new Outline()
                    .line(0, 175, 1, 0)
                    .line(131.5, 198.003, -s2, c2).fillet(2).line(131.5, 180.003, -c2, s2)
                    .line(130.265, 180.046, -cos(slope), sin(slope)).fillet(2)
                    .line(0, 198.003, -1, 0).line(0, 0, 0, -1)
                    .contour())
        }
        cut {
            direction(Vec.vY)
            position(new Vec(0, 190, 0)) { cylinder(21, 10) }
        }
    }
    (0..5).each { int i -> place(spoke) { rotate(Vec.vY, 60 * i) } }

    // Logo by +z, raised 0.5 on the outer terrace, and the same turned by 180°
    def logo = part('logo', white) {
        wireFrom(new Vec(-30.1627, 178.5, 128.5778)) {
            svg(new File(here, 'obsta-logo.svg').path, 59.5484, direction: Vec.vX, normal: Vec.vY)
        }.toFace().prism(new Vec(0, 4, 0))
        common {
            revolveY(delegate, new Outline()
                    .line(131.5, 179.003, c2, -s2).line(132, 0, 0, 1)
                    .line(131.5 + 0.5 * s2, 180.003 + 0.5 * c2, -c2, s2).line(100, 0, 0, -1)
                    .contour())
        }
    }
    [0, 180].each { a -> place(logo) { rotate(Vec.vY, a) } }

    // Screws of the cover, on the flange over the feet: M6 washer, toothed lock washer, nut, domed cap
    def screw = assembly('screw') {
        place(part('washer', cream) {
            direction(Vec.vY)
            cylinder(6, 1.6)
            cut { position(new Vec(0, -1, 0)) { cylinder(3.2, 3.6) } }
        })
        place(part('lock washer', cream) {
            // 12 teeth, a radial side then an arc of 22.79° and a slope back to the inner radius
            Closure<Vec2d> polar = { double r, double deg -> new Vec2d(r * cos(toRadians(deg)), r * sin(toRadians(deg))) }
            Contour teeth = Contour.start(4.44, 0)
            (0..11).each { int k ->
                double a = 30 * k
                Vec2d top = polar(5.5, a), end = polar(5.5, a + 22.79), next = polar(4.44, a + 30)
                teeth.lineTo(top.x, top.y).arcTo(end.x, end.y, 0, 0).lineTo(next.x, next.y)
            }
            teeth.face(delegate, atY(1.6)).prism(new Vec(0, 0.6, 0))
            cut {
                direction(Vec.vY)
                position(new Vec(0, 1, 0)) { cylinder(3.38, 2) }
            }
        })
        place(part('nut', steel) {
            direction(Vec.vY)
            position(new Vec(0, 2.2, 0)) { cylinder(5, 6) }
            topY().fillet(0.2)
            butY().fillet(0.2)
        })
        place(part('cap', grey) {
            // a dome R7.73 on an edge rounded R0.35
            double r = 0.35, dome = 7.73, domeY = -5.73
            Vec2d edge = new Vec2d(4.65, 0), center = new Vec2d(0, domeY)
            double k = dome / hypot(edge.x, edge.y - domeY)
            revolveY(delegate, Contour.start(0, 0).lineTo(edge.x + r, 0)
                    .arcTo(edge.x * k, domeY + (edge.y - domeY) * k, edge.x, edge.y)
                    .arcTo(0, domeY + dome, center.x, center.y))
        }) { translate(0, 8.2, 0) }
    }
    [[101, 101], [-101, 101], [-101, -101], [101, -101]].each { x, z -> place(screw) { translate(x, 104.003, z) } }

    // Parts of the recess, along their axis z from their base on the back of the recess
    Closure<Contour> hexagon = { double flats ->
        double r = flats / 2 / cos(PI / 6)
        Contour c = Contour.start(r, 0)
        (1..6).each { int k -> c.lineTo(r * cos(k * PI / 3), r * sin(k * PI / 3)) }
        c
    }
    // A nut across flats, its corners turned to the radius corner, and chamfered 15° to the radius chamfer on top
    Closure nut = { CadDsl dsl, double flats, double corner, Double chamfer, double z, double height ->
        hexagon(flats).face(dsl, Contour.xy(z)).prism(new Vec(0, 0, height))
        dsl.common {
            if (chamfer == null) {
                position(new Vec(0, 0, z)) { cylinder(corner, height) }
            } else {
                double depth = (corner - chamfer) / tan(toRadians(75))
                revolveZ(delegate, Contour.start(0, z).lineTo(corner, z).lineTo(corner, z + height - depth)
                        .lineTo(chamfer, z + height).lineTo(0, z + height))
            }
        }
    }
    Closure place5 = { CadDslAssembly a, Vec base -> place(a) { rotate(Vec.vX, 5); translate(base) } }

    // Cable gland: body (a nut and its neck), sealing ring, dome nut on top; g: flats, corner, base (height of its
    // nut), neck, ring, ringChamfer, domeNut (height), dome (radius), round (of the dome), top, bore
    Closure<CadDslAssembly> gland = { String name, Map<String, Double> g ->
        double ringY = 13.5, domeY = 16.5
        assembly(name) {
            place(part('body', steel) {
                nut(delegate, g.flats, g.corner, g.flats / 2, 0, g.base)
                fuse { position(new Vec(0, 0, g.base)) { cylinder(g.neck, ringY - g.base) } }
                cut { position(new Vec(0, 0, -1)) { cylinder(g.bore, ringY + 2) } }
            })
            place(part('ring', steel) {
                revolveZ(delegate, Contour.start(g.bore, ringY).lineTo(g.ring - g.ringChamfer, ringY)
                        .lineTo(g.ring, ringY + g.ringChamfer).lineTo(g.ring, domeY).lineTo(g.bore, domeY))
            })
            place(part('dome nut', steel) {
                double nutTop = domeY + g.domeNut
                nut(delegate, g.flats, g.corner, g.dome, domeY, g.domeNut)
                fuse {
                    revolveZ(delegate, Contour.start(0, nutTop).lineTo(g.dome, nutTop).lineTo(g.dome, g.top - g.round)
                            .arcTo(g.dome - g.round, g.top, g.dome - g.round, g.top - g.round).lineTo(0, g.top))
                }
                cut { position(new Vec(0, 0, domeY - 1)) { cylinder(g.bore, g.top) } }
            })
        }
    }
    place5(gland('cable gland M20', [flats: 29, corner: 15.95, base: 4.5, neck: 13.5, ring: 14.21, ringChamfer: 0.355,
                                     domeNut: 4, dome: 14.49, round: 4, top: 30.5, bore: 4.5]), bigGland)
    place5(gland('cable gland M16', [flats: 24, corner: 13.2, base: 4, neck: 11, ring: 11.76, ringChamfer: 0.38,
                                     domeNut: 3.5, dome: 11.99, round: 3.5, top: 29, bore: 3.5]), smallGland)

    // Vent: a nut 22 across flats under a cap rounded R1, crossed by three holes R1.5
    place5(assembly('vent') {
        place(part('vent', steel) {
            nut(delegate, 22, 12.25, null, 0, 5)
            fuse {
                revolveZ(delegate, Contour.start(0, 5).lineTo(9.5, 5).lineTo(9.5, 9.5).arcTo(8.5, 10.5, 8.5, 9.5)
                        .lineTo(0, 10.5))
            }
            cut {
                [90, 210, 330].each { int a ->
                    Vec across = new Vec(cos(toRadians(a)), sin(toRadians(a)), 0)
                    direction(across)
                    position(new Vec(0, 0, 7.5) - across * 12) { cylinder(1.5, 24) }
                }
            }
        })
    }, vent)

    // M12 connector: a nut 17 across flats, a sleeve (R0.3 fillet, chamfered top), an insert with a keyed socket,
    // and five pins
    place5(assembly('M12 connector') {
        place(part('connector', steel) {
            nut(delegate, 17, 9, null, 0, 2)
            fuse {
                revolveZ(delegate, Contour.start(0, 2).lineTo(5.75, 2).arcTo(5.45, 2.3, 5.75, 2.3).lineTo(5.45, 11.8)
                        .lineTo(5.25, 12).lineTo(5.35, 12).lineTo(5.35, 13).lineTo(0, 13))
            }
            cut {
                revolveZ(delegate, Contour.start(0, 4.1).lineTo(4.15, 4.1).lineTo(4.15, 12.5).lineTo(4.284, 13)
                        .lineTo(4.284, 13.1).lineTo(0, 13.1))
            }
            fuse {
                position(new Vec(3.55 * cos(-PI / 4), 3.55 * sin(-PI / 4), 4)) { cylinder(0.6, 8.5) }
            }
        })
        def pin = part('pin', steel) {
            cylinder(0.5, 6.4)
            fuse { position(new Vec(0, 0, 6.4)) { sphere(0.5) } }
        }
        [[0, 0], [2.5, 0], [0, 2.5], [-2.5, 0], [0, -2.5]].each { x, y -> place(pin) { translate(x, y, 4.1) } }
    }, connector)

    // Label of the side +x, 0.1 thick on its 2° slope, the text printed 0.005 thick: (u, v) on the plane of the side,
    // u along -z, v up the slope
    Vec n = new Vec(c2, s2, 0), u = new Vec(0, 0, -1), v = new Vec(-s2, c2, 0)
    double side = 137.408           // the plane of the side, n.p = side
    place(part('label', labelBlue) {
        Contour.roundedRectangle(57, 27, 3).face(delegate, { Vec2d p -> u * p.x + v * (p.y + 34.7046) + n * side })
                .prism(n * 0.1)
    })
    place(part('label text', Color.BLACK) {
        wireFrom(u * -27.5144 + v * 23.0365 + n * (side + 0.1)) {
            svg(new File(here, 'ofd-label.svg').path, 45.1770, direction: u, normal: n)
        }.toFace().prism(n * 0.005)
    })
}.display(args ? args[0] : null)
