package com

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.ConventionTask
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
abstract class ExtraTransform : DefaultTask() {
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    abstract val inputFile: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: RegularFileProperty

    @get:Classpath
    @get:InputFiles
    abstract val classpath: ConfigurableFileCollection

    @TaskAction
    fun transform() {
        inputFile.files.forEach { inputDir ->
            Transformer(inputDir, outputDir.get().asFile).let { t ->
               t.transform()
            }
        }
    }

}

class Transformer(
    inputDir: File,
    outputDir: File = inputDir
) : TransformerBase(inputDir, outputDir) {

    fun transform() {

        val files = inputDir.walk().filter { it.isFile }.toList()
        files.forEach { file ->
            file.toOutputFile().mkdirsAndWrite(file.readBytes())

        }
    }
}

abstract class TransformerBase(
    var inputDir: File,
    var outputDir: File
) {
    protected fun File.toOutputFile(): File = outputDir / relativeTo(inputDir).toString()

    protected fun File.mkdirsAndWrite(outBytes: ByteArray) {
        parentFile.mkdirs()
        writeBytes(outBytes)
    }

    protected operator fun File.div(child: String) =
        File(this, child)

}
