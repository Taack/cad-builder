package org.taack.cad.dsl

import groovy.transform.CompileStatic

/**
 * Settings of a plate surface, see {@link CadDsl#plate(Map, Closure)}.
 */
@CompileStatic
final class PlateOptions {
    static final int DEFAULT_DEGREE = 3
    static final int DEFAULT_POINTS_ON_CURVE = 15
    static final int DEFAULT_ITERATIONS = 2
    static final double DEFAULT_TOLERANCE = 1.0e-4d
    static final int DEFAULT_MAX_SEGMENTS = 9
    static final int DEFAULT_MAX_DEGREE = 8

    private static final Set<String> NAMES = ['degree', 'pointsOnCurve', 'iterations', 'tolerance', 'maxSegments', 'maxDegree'] as Set<String>

    /** Degree of the plate surface */
    final int degree
    /** Points sampled on each curve constraint */
    final int pointsOnCurve
    /** Solving iterations */
    final int iterations
    /** Tolerance of the BSpline approximating the plate surface */
    final double tolerance
    /** Maximum number of BSpline segments */
    final int maxSegments
    /** Maximum BSpline degree */
    final int maxDegree

    PlateOptions(Map<String, ? extends Number> options = [:]) {
        Set<String> unknown = options.keySet() - NAMES
        if (unknown) throw new IllegalArgumentException("plate: unknown options $unknown, expected some of $NAMES")
        degree = option(options, 'degree', DEFAULT_DEGREE).intValue()
        pointsOnCurve = option(options, 'pointsOnCurve', DEFAULT_POINTS_ON_CURVE).intValue()
        iterations = option(options, 'iterations', DEFAULT_ITERATIONS).intValue()
        tolerance = option(options, 'tolerance', DEFAULT_TOLERANCE).doubleValue()
        maxSegments = option(options, 'maxSegments', DEFAULT_MAX_SEGMENTS).intValue()
        maxDegree = option(options, 'maxDegree', DEFAULT_MAX_DEGREE).intValue()
    }

    private static Number option(Map<String, ? extends Number> options, String name, Number defaultValue) {
        options.containsKey(name) ? options.get(name) : defaultValue
    }

    @Override
    String toString() {
        "PlateOptions{degree=$degree, pointsOnCurve=$pointsOnCurve, iterations=$iterations, tolerance=$tolerance, maxSegments=$maxSegments, maxDegree=$maxDegree}"
    }
}
