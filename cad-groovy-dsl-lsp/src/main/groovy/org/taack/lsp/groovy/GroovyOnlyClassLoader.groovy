package org.taack.lsp.groovy

import groovy.transform.CompileStatic

/**
 * A loader of the JDK and of Groovy only, to compile the code being edited apart from the classes of the application
 * running the engine: that code then sees the classes of its DSL only, and the global AST transformations of the
 * application do not run on it.
 */
@CompileStatic
class GroovyOnlyClassLoader extends ClassLoader {
    private static final List<String> PACKAGES = ['groovy/', 'groovyjarjar', 'org/codehaus/groovy/', 'org/apache/groovy/']
    private final ClassLoader groovyLoader
    private final String groovyJar

    GroovyOnlyClassLoader() {
        super(ClassLoader.platformClassLoader)
        this.groovyLoader = GroovyObject.classLoader
        this.groovyJar = GroovyObject.protectionDomain.codeSource?.location?.toString()
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (!isGroovy(name.replace('.', '/'))) throw new ClassNotFoundException(name)
        groovyLoader.loadClass(name)
    }

    @Override
    protected URL findResource(String name) {
        isGroovy(name) ? groovyLoader.getResource(name) : findResources(name).with { it.hasMoreElements() ? it.nextElement() : null }
    }

    /**
     * The resources of the Groovy jar only, among them its extension modules and global AST transformations
     */
    @Override
    protected Enumeration<URL> findResources(String name) {
        List<URL> res = groovyLoader.getResources(name).toList().findAll { URL u ->
            isGroovy(name) || (groovyJar != null && u.toString().contains(groovyJar))
        }
        Collections.enumeration(res)
    }

    private static boolean isGroovy(String path) {
        PACKAGES.any { path.startsWith(it) }
    }
}
