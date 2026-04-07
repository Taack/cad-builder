package org.taack.cad.dsl.geom

import java.lang.foreign.MemorySegment

interface IClosedShape2d extends IConstruction {
    MemorySegment make2dCurve()
    boolean getReverse()
}