plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

sourceSets.all {
    val classesDirs = (output.classesDirs as ConfigurableFileCollection).from as Collection<Any>
    val originalClassesDirs = files(classesDirs.toTypedArray()).filter { it.exists() }
    val transformedClassesDir = project.layout.buildDirectory.file("classes/extratransform/${name}")
    val transform = tasks.register<com.ExtraTransform>(getTaskName("transform", "extraTransform")) {
        dependsOn(classesTaskName)

        classpath.setFrom(compileClasspath)
        inputFile.setFrom(originalClassesDirs)
        outputDir.set(transformedClassesDir)
    }

    (output.classesDirs as ConfigurableFileCollection).setFrom(transformedClassesDir)
    compiledBy(transform)

    (tasks.findByName(jarTaskName) as? Jar)?.apply {

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
