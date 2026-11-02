package org.taack.lsp.groovy.fixture

import groovy.transform.CompileStatic

/**
 * A small DSL with closures delegating to the builders, for the tests
 */
@CompileStatic
class Html {
    static Html html(@DelegatesTo(value = Html, strategy = Closure.DELEGATE_FIRST) Closure c) {
        Html h = new Html()
        c.delegate = h
        c.call()
        h
    }

    void head(@DelegatesTo(value = Head, strategy = Closure.DELEGATE_FIRST) Closure c) {}

    Body body(@DelegatesTo(value = Body, strategy = Closure.DELEGATE_FIRST) Closure c) { new Body() }

    /** The closure delegates to the target, whatever its type */
    static <T> T configure(@DelegatesTo.Target T target, @DelegatesTo(strategy = Closure.DELEGATE_FIRST) Closure c) { target }
}

@CompileStatic
class Head {
    void title(String title) {}

    void meta(String name, String content) {}
}

@CompileStatic
class Body {
    String cssClass

    Paragraph p(String text) { new Paragraph() }

    void div(@DelegatesTo(value = Body, strategy = Closure.DELEGATE_FIRST) Closure c) {}

    void table(@DelegatesTo(value = Table, strategy = Closure.DELEGATE_ONLY) Closure c) {}
}

@CompileStatic
class Paragraph {
    Paragraph bold() { this }

    Paragraph italic() { this }
}

@CompileStatic
class Table {
    void row(List<String> cells) {}
}
