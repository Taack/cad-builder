import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadDsl
import org.taack.cad.dsl.CadDslAssembly
import org.taack.cad.dsl.CadDslFace
import org.taack.cad.dsl.CadDslSolid
import org.taack.cad.dsl.Color
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Outline
import org.taack.cad.dsl.geom.Vec

import static org.taack.cad.dsl.CadDsl.assembly
import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.cad.dsl.CadDsl.param

/**
 * The NEMA 17 stepper motor 17HS24-2104S, converted from its STEP file (17HS24-2104S.STEP), in millimeters, with its
 * colors.
 * <p>
 * The shaft is the Z axis, the rear face of the motor is the plane z = 0, the front face the plane z = length. The
 * connector is on the -Y side.
 * <p>
 * The motor is an assembly, a part for each piece: the stator and the two end caps, the shaft, the four screws of the
 * rear cap, the connector and its four wires. Each part is built by its own method, in the coordinates of the motor.
 */
@CompileStatic
class Stepper17HS24 {
    private static final Color HOUSING = Color.rgb(64, 64, 64)
    private static final Color STEEL = Color.WHITE
    private static final Color PLASTIC = Color.WHITE

    // Stator: the stack of laminations, a square clipped by a circle at its corners
    private static final double STATOR_SIDE = 42.32
    private static final double STATOR_CORNER_RADIUS = 25
    private static final double STATOR_FROM_REAR = 8
    private static final double STATOR_TO_FRONT = 6

    // End caps: a square with its corners cut, 8 mm thick at the front, and 10 mm at the rear
    private static final double CAP_SIDE = 42.3
    private static final double CAP_CORNER_CUT = 38.3 // x + y of the line cutting the corners
    private static final double CAP_RADIUS = 0.5 // of the rounded edges of the caps
    private static final double REAR_CAP_THICKNESS = 10
    private static final double FRONT_CAP_THICKNESS = 8

    // Pilot boss on the front face, with a recessed ring around it
    private static final double BOSS_RADIUS = 11
    private static final double BOSS_HEIGHT = 2
    private static final double RECESS_RADIUS = 16
    private static final double RECESS_DEPTH = 0.5

    // Shaft, and the bores of the caps around it
    private static final double SHAFT_RADIUS = 2.5
    private static final double SHAFT_CHAMFER = 0.5
    private static final double SHAFT_FLAT_Y = 2 // the flat of the shaft tip is the plane y = 2
    private static final double SHAFT_REAR_END = 2 // where the shaft ends behind the rear face
    private static final double BEARING_RADIUS = 2.8
    private static final double BEARING_BORE_RADIUS = 3.5

    // The four M3 screws, at the corners of a 31 mm square
    private static final double SCREW_SPACING = 31
    private static final double TAPPED_HOLE_RADIUS = 1.25
    private static final double TAPPED_HOLE_DEPTH = 4.5
    private static final double TAPPED_HOLE_TIP = 0.75 // the conical end of the drill
    private static final double COUNTERBORE_RADIUS = 3
    private static final double COUNTERBORE_DEPTH = 2
    private static final double SCREW_HEAD_RADIUS = 2.3
    private static final double SCREW_HEAD_HEIGHT = 1.5
    private static final double SCREW_HEAD_ROUNDING = 1

    // Connector, in a pocket of the -Y side of the motor, and the wires coming out of it
    private static final double POCKET_FLOOR_Y = -19.15
    private static final double CONNECTOR_FRONT_Y = -21.25
    private static final double WIRE_RADIUS = 0.56

    /**
     * The motor, as an assembly of its parts
     */
    static CadDslAssembly motor() {
        double length = param('length', 60) // of the body, from the rear face to the front face
        double shaftLength = param('shaftLength', 24) // from the front face to the tip of the shaft
        double flatLength = param('flatLength', 15) // of the flat of the shaft tip
        double wireLength = param('wireLength', 20) // from the connector

        assembly('Stepper Motor 17HS24-2104S') {
            place(part(rearCap(), 'Rear Cap', HOUSING))
            place(part(stator(length), 'Stator', HOUSING))
            place(part(frontCap(length), 'Front Cap', HOUSING))
            place(part(shaft(length + shaftLength, flatLength), 'Shaft', STEEL))
            def screw = part(rearScrew(), 'Rear Screw', STEEL)
            screwPositions().each { Vec position ->
                place(screw) { translate(position) }
            }
            place(part(connector(), 'Connector', PLASTIC))
            place(part(wire(-1.36d, 5.084d, wireLength), 'Green Wire', Color.rgb(0, 128, 64)))
            place(part(wire(1.36d, 5.084d, wireLength), 'Red Wire', Color.RED))
            place(part(wire(-1.36d, 6.416d, wireLength), 'Blue Wire', Color.rgb(0, 128, 255)))
            place(part(wire(1.36d, 6.416d, wireLength), 'Black Wire', Color.BLACK))
        }
    }

    /**
     * The stack of laminations, from the rear cap to the front one: a square, its corners clipped by a circle, with the
     * hole of the shaft
     */
    private static CadDslSolid stator(double length) {
        double height = length - STATOR_FROM_REAR - STATOR_TO_FRONT
        CadDsl stator = cd()
        stator.position(new Vec(-STATOR_SIDE / 2, -STATOR_SIDE / 2, STATOR_FROM_REAR)) {
            box(STATOR_SIDE, STATOR_SIDE, height)
        }.common {
            position(new Vec(0, 0, STATOR_FROM_REAR)) {
                cylinder(STATOR_CORNER_RADIUS, height)
            }
        }
        shaftHole(stator, STATOR_FROM_REAR, height)
        stator
    }

    /**
     * The square of an end cap, its corners cut by a line and rounded: a quarter, turned 4 times
     */
    private static Outline capOutline() {
        double half = CAP_SIDE / 2
        double corner = CAP_CORNER_CUT / 2
        new Outline()
                .line(half, 0, 0, 1).fillet(CAP_RADIUS)
                .line(corner, corner, -1, 1).fillet(CAP_RADIUS)
                .quarterTurns()
    }

    /**
     * The rear cap: its corners go on 2 mm along the stator, the middle is left to it. It has the bore of the end of the
     * shaft, the counterbores of the screws and the pocket of the connector.
     */
    private static CadDslSolid rearCap() {
        CadDsl cap = cd()
        cap.face(Frame.xy(0), capOutline()).prism(REAR_CAP_THICKNESS)
        roundedEdges(cap) { butZ() }
        leaveRoomForStator(cap, STATOR_FROM_REAR, REAR_CAP_THICKNESS - STATOR_FROM_REAR)
        shaftHole(cap, 0, REAR_CAP_THICKNESS)
        rearBore(cap)
        counterbores(cap)
        connectorPocket(cap)
        cap
    }

    /**
     * The front cap: its corners go on 2 mm along the stator. It has the pilot boss, the bore of the bearing and the
     * tapped holes.
     */
    private static CadDslSolid frontCap(double length) {
        double from = length - FRONT_CAP_THICKNESS
        CadDsl cap = cd()
        cap.face(Frame.xy(from), capOutline()).prism(FRONT_CAP_THICKNESS)
        roundedEdges(cap) { topZ() }
        leaveRoomForStator(cap, from, length - STATOR_TO_FRONT - from)
        frontBoss(cap, length)
        shaftHole(cap, from, FRONT_CAP_THICKNESS + BOSS_HEIGHT)
        tappedHoles(cap, length)
        cap
    }

    /**
     * The stator stands where the cap and it overlap: the cap keeps its corners only
     */
    private static void leaveRoomForStator(CadDsl cap, double from, double height) {
        cap.cut {
            position(new Vec(0, 0, from)) {
                cylinder(STATOR_CORNER_RADIUS, height)
            }
        }
    }

    /**
     * Rounds the edges of the end face selected. Selecting a face moves the current position to it, and the current
     * direction to its normal: the selection is made in a position of its own, and the direction is set back to Z, so
     * that the next solids are placed from the origin along Z.
     */
    private static void roundedEdges(CadDsl cap, @DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure<CadDslFace> endFace) {
        cap.position(new Vec()) {
            endFace.delegate = cap
            endFace.call().fillet(CAP_RADIUS)
        }
        cap.direction(Vec.vZ)
    }

    /**
     * The hole where the shaft goes through a part
     */
    private static void shaftHole(CadDsl part, double from, double height) {
        part.cut {
            position(new Vec(0, 0, from - 1)) {
                cylinder(SHAFT_RADIUS, height + 2)
            }
        }
    }

    /**
     * The pilot boss of the front face, rising 2 mm above it, and the ring recessed around it. Its bore holds the
     * bearing of the shaft: wide at the top, then narrower down to the shaft.
     */
    private static void frontBoss(CadDsl cap, double length) {
        Vec axis = new Vec()
        double top = length + BOSS_HEIGHT
        // the ring, with its bottom and its sides rounded
        cap.cut {
            wireFrom(section(axis, BOSS_RADIUS, length)) {
                arcAround(section(axis, BOSS_RADIUS + RECESS_DEPTH, length - RECESS_DEPTH), section(axis, BOSS_RADIUS + RECESS_DEPTH, length))
                edge(section(axis, RECESS_RADIUS - RECESS_DEPTH, length - RECESS_DEPTH))
                arcAround(section(axis, RECESS_RADIUS, length), section(axis, RECESS_RADIUS - RECESS_DEPTH, length))
                edge(section(axis, RECESS_RADIUS, length + 1))
                edge(section(axis, BOSS_RADIUS, length + 1))
                edge(section(axis, BOSS_RADIUS, length))
            }.toFace().revolution(axis, Vec.vZ)
        }
        // the boss, its top edge rounded
        cap.fuse {
            wireFrom(section(axis, 0, length - RECESS_DEPTH)) {
                edge(section(axis, BOSS_RADIUS, length - RECESS_DEPTH))
                edge(section(axis, BOSS_RADIUS, top - CAP_RADIUS))
                arcAround(section(axis, BOSS_RADIUS - CAP_RADIUS, top), section(axis, BOSS_RADIUS - CAP_RADIUS, top - CAP_RADIUS))
                edge(section(axis, 0, top))
                edge(section(axis, 0, length - RECESS_DEPTH))
            }.toFace().revolution(axis, Vec.vZ)
        }
        // the bore of the bearing, its mouth rounded: 3.5 mm in radius, then 2.8 mm 1 mm lower
        cap.cut {
            wireFrom(section(axis, 0, length - 1)) {
                edge(section(axis, BEARING_RADIUS, length - 1))
                edge(section(axis, BEARING_RADIUS, length))
                edge(section(axis, BEARING_BORE_RADIUS, length))
                edge(section(axis, BEARING_BORE_RADIUS, top - CAP_RADIUS))
                arcAround(section(axis, BEARING_BORE_RADIUS + CAP_RADIUS, top), section(axis, BEARING_BORE_RADIUS + CAP_RADIUS, top - CAP_RADIUS))
                edge(section(axis, BEARING_BORE_RADIUS + CAP_RADIUS, top + 1))
                edge(section(axis, 0, top + 1))
                edge(section(axis, 0, length - 1))
            }.toFace().revolution(axis, Vec.vZ)
        }
    }

    /**
     * The bore of the rear cap, around the end of the shaft: the same bearing as the front one, its mouth rounded
     */
    private static void rearBore(CadDsl cap) {
        Vec axis = new Vec()
        cap.cut {
            wireFrom(section(axis, 0, SHAFT_REAR_END + 1)) {
                edge(section(axis, BEARING_RADIUS, SHAFT_REAR_END + 1))
                edge(section(axis, BEARING_RADIUS, SHAFT_REAR_END))
                edge(section(axis, BEARING_BORE_RADIUS, SHAFT_REAR_END))
                edge(section(axis, BEARING_BORE_RADIUS, CAP_RADIUS))
                arcAround(section(axis, BEARING_BORE_RADIUS + CAP_RADIUS, 0), section(axis, BEARING_BORE_RADIUS + CAP_RADIUS, CAP_RADIUS))
                edge(section(axis, BEARING_BORE_RADIUS + CAP_RADIUS, -1))
                edge(section(axis, 0, -1))
                edge(section(axis, 0, SHAFT_REAR_END + 1))
            }.toFace().revolution(axis, Vec.vZ)
        }
    }

    /**
     * The shaft, from its rear end to its tip, both chamfered, with a flat on the tip
     */
    private static CadDslSolid shaft(double tip, double flatLength) {
        Vec axis = new Vec()
        CadDsl shaft = cd()
        shaft.wireFrom(section(axis, 0, SHAFT_REAR_END)) {
            edge(section(axis, SHAFT_RADIUS - SHAFT_CHAMFER, SHAFT_REAR_END))
            edge(section(axis, SHAFT_RADIUS, SHAFT_REAR_END + SHAFT_CHAMFER))
            edge(section(axis, SHAFT_RADIUS, tip - SHAFT_CHAMFER))
            edge(section(axis, SHAFT_RADIUS - SHAFT_CHAMFER, tip))
            edge(section(axis, 0, tip))
            edge(section(axis, 0, SHAFT_REAR_END))
        }.toFace().revolution(axis, Vec.vZ)
        shaft.cut {
            position(new Vec(-SHAFT_RADIUS, SHAFT_FLAT_Y, tip - flatLength)) {
                box(2 * SHAFT_RADIUS, SHAFT_RADIUS, flatLength + 1)
            }
        }
        shaft
    }

    /**
     * The M3 tapped holes of the front cap, ending in the cone of the drill
     */
    private static void tappedHoles(CadDsl cap, double length) {
        cap.cut {
            screwPositions().each { Vec screw ->
                position(new Vec(screw.x, screw.y, length - TAPPED_HOLE_DEPTH)) {
                    cylinder(TAPPED_HOLE_RADIUS, TAPPED_HOLE_DEPTH + 1)
                }
                position(new Vec(screw.x, screw.y, length - TAPPED_HOLE_DEPTH - TAPPED_HOLE_TIP)) {
                    cone(0, TAPPED_HOLE_RADIUS, TAPPED_HOLE_TIP)
                }
            }
        }
    }

    /**
     * The counterbores of the rear cap, where the screws are sunk: 3 mm in radius, 2 mm deep, their mouth rounded
     */
    private static void counterbores(CadDsl cap) {
        cap.cut {
            screwPositions().each { Vec screw ->
                wireFrom(section(screw, 0, -1)) {
                    edge(section(screw, COUNTERBORE_RADIUS + CAP_RADIUS, -1))
                    edge(section(screw, COUNTERBORE_RADIUS + CAP_RADIUS, 0))
                    arcAround(section(screw, COUNTERBORE_RADIUS, CAP_RADIUS), section(screw, COUNTERBORE_RADIUS + CAP_RADIUS, CAP_RADIUS))
                    edge(section(screw, COUNTERBORE_RADIUS, COUNTERBORE_DEPTH))
                    edge(section(screw, 0, COUNTERBORE_DEPTH))
                    edge(section(screw, 0, -1))
                }.toFace().revolution(screw, Vec.vZ)
            }
        }
    }

    /**
     * A screw holding the motor together, around the Z axis: a head sitting on the bottom of a counterbore, its top
     * rounded, with a cross socket. It is placed on each of the counterbores.
     */
    private static CadDslSolid rearScrew() {
        Vec axis = new Vec()
        double top = COUNTERBORE_DEPTH - SCREW_HEAD_HEIGHT
        CadDsl screw = cd()
        screw.wireFrom(section(axis, 0, COUNTERBORE_DEPTH)) {
            edge(section(axis, SCREW_HEAD_RADIUS, COUNTERBORE_DEPTH))
            edge(section(axis, SCREW_HEAD_RADIUS, top + SCREW_HEAD_ROUNDING))
            arcAround(section(axis, SCREW_HEAD_RADIUS - SCREW_HEAD_ROUNDING, top), section(axis, SCREW_HEAD_RADIUS - SCREW_HEAD_ROUNDING, top + SCREW_HEAD_ROUNDING))
            edge(section(axis, 0, top))
            edge(section(axis, 0, COUNTERBORE_DEPTH))
        }.toFace().revolution(axis, Vec.vZ)
        // the cross: two slots, 0.9 mm wide, 1 mm deep
        screw.cut {
            position(new Vec(-1.364, -0.444, 0)) {
                box(2.728, 0.888, top + 1)
            }
            position(new Vec(-0.472, -1.45, 0)) {
                box(0.944, 2.9, top + 1)
            }
        }
        screw
    }

    /**
     * The pocket in the -Y side of the rear cap, 2 mm deep, where the connector sits
     */
    private static void connectorPocket(CadDsl cap) {
        cap.cut {
            position(new Vec(-3.6, POCKET_FLOOR_Y - 3, 3.5)) {
                box(7.2, 3, 4.5)
            }
        }
    }

    /**
     * The plug in the pocket, standing 0.1 mm out of it, hollow on its front where the wires come out
     */
    private static CadDslSolid connector() {
        CadDsl plug = cd()
        plug.position(new Vec(-3.5, CONNECTOR_FRONT_Y, 3.6)) {
            box(7, POCKET_FLOOR_Y - CONNECTOR_FRONT_Y, 4.14)
        }
        plug.cut {
            position(new Vec(-2.6, CONNECTOR_FRONT_Y - 1, 4.44)) {
                box(5.2, POCKET_FLOOR_Y - CONNECTOR_FRONT_Y + 1, 2.47)
            }
        }
        plug
    }

    /**
     * A wire, from the bottom of the plug going away along -Y
     * @param x, z Where it comes out of the plug
     */
    private static CadDslSolid wire(double x, double z, double length) {
        CadDsl wire = cd()
        wire.direction(new Vec(0, -1, 0))
        wire.position(new Vec(x, POCKET_FLOOR_Y, z)) {
            cylinder(WIRE_RADIUS, length)
        }
        wire
    }

    /**
     * The centers of the four screws, in the plane z = 0
     */
    private static List<Vec> screwPositions() {
        double half = SCREW_SPACING / 2
        [new Vec(-half, -half, 0), new Vec(half, -half, 0), new Vec(half, half, 0), new Vec(-half, half, 0)]
    }

    /**
     * The point of the section of a solid of revolution around the axis Z through axis, at the radius r and the height z:
     * the sections are drawn in the plane through the axis and the X direction
     */
    private static Vec section(Vec axis, double r, double z) {
        new Vec(axis.x + r, axis.y, z)
    }
}
