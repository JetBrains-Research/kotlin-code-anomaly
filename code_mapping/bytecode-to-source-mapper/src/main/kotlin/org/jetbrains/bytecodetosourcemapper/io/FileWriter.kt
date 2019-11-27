package org.jetbrains.bytecodetosourcemapper.io

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

object FileWriter {
    fun write(file: File, dirPath: String, targetDirPath: String, content: Any) {
        val relativePath = file.relativeTo(File(dirPath))
        val outputPath = File("$targetDirPath/$relativePath")
        val mapper = ObjectMapper()

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        File("$targetDirPath/${relativePath.parent ?: ""}").mkdirs()
        outputPath.writeText(mapper.writeValueAsString(content))
    }

    fun write(filename: String, dirPath: String, targetDirPath: String, content: Any) {
        this.write(File(filename), dirPath, targetDirPath, content)
    }

    fun write(filename: String, content: Any) {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        File(filename).writeText(mapper.writeValueAsString(content))
    }

    fun write(file: File, content: Any) {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        file.writeText(mapper.writeValueAsString(content))
    }
}