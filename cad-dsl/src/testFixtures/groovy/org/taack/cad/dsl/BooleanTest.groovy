package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec

import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class BooleanTest {


    @Test
    void "Test cut 2 boxes"() {
        cd().box(1, 1, 1).cut {
            position(new Vec(1, 1, 1) * 0.5) {
                box(1, 1, 1)
            }
        }.display()
    }

    @Test
    void "Test cut 3 boxes"() {
        cd().box(1, 1, 1).cut {
            position(new Vec(1, 1, 1) * 0.6) {
                box(1, 1, 1)
            }
            position(new Vec(1, 1, 1) * -0.6) {
                box(1, 1, 1)
            }
        }.display()
    }

    @Test
    void "Test fuse 3 boxes"() {
        cd().box(1, 1, 1).fuse {
            position(new Vec(1, 1, 1) * 0.6) {
                box(1, 1, 1)
            }
            position(new Vec(1, 1, 1) * -0.6) {
                box(1, 1, 1)
            }
        }.display()
    }

    @Test
    void "Test common 3 boxes"() {
        cd().box(1, 1, 1).common {
            position(new Vec(1.2, 1, 1) * 0.4) {
                direction(new Vec(1, 1, 1))
                box(1, 1, 1)
            }
            position(new Vec(1, 1, 1) * 0.2) {
                box(1, 1, 1)
            }
        }.display()
    }

    @Test
    void "Test fuse 3 boxes with direction"() {
        cd().box(1, 1, 1).fuse {
            position(new Vec(1, 1, 1) * 0.6) {
                direction(new Vec(1, 1, 1))
                box(1, 1, 1)
            }
            position(new Vec(1, 1, 1) * -0.1) {
                box(1, 1, 1)
            }
        }.display()
    }

    @Test
    void "Test mix fuse - common - cut"() {
        Vec diagonal = new Vec(1, 1, 1)

        cd().box(2, 2, 1).cut {
            position(diagonal * 0.4) {
                box(1, 1, 1)
                position(diagonal * 0.3) {
                    cylinder(1 / 2, 3)
                }
            }
            position(diagonal * 0.6) {
                box(1, 1, 1)
            }
        }.display()
    }

    @Test
    void "Test mix fuse - common - cut bis"() {
        cd().box(1, 1, 1).common {
            box(1, 1, 1).cut {
                position(new Vec(1, 1, 1) * 0.3) {
                    cylinder(1 / 2, 3)
                }
            }
            direction(new Vec(0.1, 2, 1))
            box(1, 1, 1)
        }.display()
    }

    @Test
    void "Test mix fuse - common - cut ter"() {
        cd().box(1, 1, 1).fuse {
            position(new Vec(1, 1, 1) * 0.4) {
                box(1, 1, 1).cut {
                    position(new Vec(1, 1, 1) * 0.3) {
                        cylinder(1 / 3, 3)
                    }
                }
            }
            position(new Vec(1, 1, 1) * 0.6) {
                box(1, 1, 1)
            }
        }.display()
    }

    @Test
    void "Test mix fuse - common - cut quad"() {
        cd().box(1, 1, 1).cut {
            position(new Vec(1, 1, 1) * 0.3) {
                cylinder(1 / 3, 3)
            }
        }.display()
    }

    @Test
    void "Test mix fuse - common - cut cinq"() {
        cd().box(1 / 3, 3, 1).cut {
            position(new Vec(1, 1, 1) * 0.3) {
                box(1, 1, 1)
            }
        }.display()
    }


}