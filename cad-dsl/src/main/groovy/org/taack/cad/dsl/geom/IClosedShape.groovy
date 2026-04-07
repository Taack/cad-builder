package org.taack.cad.dsl.geom

import java.lang.foreign.MemorySegment

interface IClosedShape {
        MemorySegment makeWireAdd(MemorySegment makeWire)
}