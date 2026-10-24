package org.taack.cad.dsl

import groovy.transform.CompileStatic

/**
 * Named parameters of the models, declared with {@link CadDsl#param}.
 *
 * A parameter is a plain number for the DSL; kernels may use the names: the FreeCAD one writes them in the Params
 * spreadsheet of the FCStd files it saves, and binds each dimension equal to a parameter to its cell.
 * A parameter can be set from outside the script with the system property cad.param.&lt;name&gt;.
 */
@CompileStatic
final class CadParams {
    private static final Map<String, Double> VALUES = Collections.synchronizedMap(new LinkedHashMap<String, Double>())

    private CadParams() {}

    /**
     * Declare a parameter, or give it a new value
     * @return Its value: the cad.param.&lt;name&gt; system property when it is set, else value
     */
    static double param(String name, Number value) {
        String override = System.getProperty("cad.param.$name")
        double v = override ? Double.parseDouble(override) : value.toDouble()
        VALUES.put(name, v)
        v
    }

    /**
     * @return The parameters declared, in order
     */
    static Map<String, Double> values() {
        synchronized (VALUES) {
            new LinkedHashMap<String, Double>(VALUES)
        }
    }

    /**
     * Forget the parameters, e.g. before running another script
     */
    static void clear() {
        VALUES.clear()
    }
}
