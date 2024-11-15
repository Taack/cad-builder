package org.taack.cad.dsl.builder

import org.taack.cad.dsl.Vec

interface ICad {
    ICad move(Vec p)
    ICad rotate(Vec p)

}