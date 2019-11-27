package org.jetbrains.bytecodesnomaliessourcefinder.io

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

object FileWriter {
    fun write(file: File, dirPath: String, targetDirPath: String, content: Any) {
        val relativePath = file.relativeTo(File(dirPath))
        val outputPath = File("$targetDirPath/$relativePath")

        File("$targetDirPath/${relativePath.parent ?: ""}").mkdirs()
        outputPath.writeText(ObjectMapper().writeValueAsString(content))
    }

    fun write(filename: String, dirPath: String, targetDirPath: String, content: Any) {
        this.write(File(filename), dirPath, targetDirPath, content)
    }

    fun write(filename: String, content: Any) {
        File(filename).writeText(ObjectMapper().writeValueAsString(content))
    }
}