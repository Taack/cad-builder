package org.taack.cad.dsl.dump.occnative


import org.taack.cad.dsl.builder.IProfile
import org.taack.cad.dsl.builder.IWire
import org.taack.cad.dsl.builder.Vec2d

class Profile implements Selector, IProfile {
    @Override
    IProfile move(BigDecimal x, BigDecimal y) {
        return null
    }

    @Override
    IProfile move(Vec2d from) {
        return null
    }

    @Override
    IProfile lineTo(BigDecimal toX, BigDecimal toY) {
        return null
    }

    @Override
    IProfile lineTo(Vec2d to) {
        return null
    }

    @Override
    IProfile threePointArc(Vec2d p2, Vec2d p3) {
        return null
    }

    @Override
    IProfile circle(BigDecimal radius) {
        return null
    }

    @Override
    IProfile radiusArc(Vec2d sx, BigDecimal radius) {
        return null
    }

    @Override
    IProfile close() {
        return null
    }

    @Override
    IProfile rect(BigDecimal sx, BigDecimal sy) {
        return null
    }

    @Override
    IProfile union(IProfile... profiles) {
        return null
    }

    @Override
    IProfile center() {
        return null
    }

    @Override
    IWire toWire() {
        return null
    }
}
