package org.taack.cad.dsl.geom

import java.lang.foreign.MemorySegment

interface IClosedShape2d {
    MemorySegment make2dCurve()
    boolean getReverse()
}