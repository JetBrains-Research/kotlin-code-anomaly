package org.jetbrains.ngramgenerator.io

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

class JsonFilesReader<T>(
        private val dirPath: String,
        private val filesExt: String,
        private val entityType: TypeReference<*>) {
    private fun readFile(file: File): T {
        return jacksonObjectMapper().readValue(file.readText(), entityType)
    }

    private fun walkDirectory(additionalFileCheck: ((File) -> Boolean)? = null, callback: (T, File) -> Unit) {
        File(dirPath).walkTopDown().forEach {
            if (it.isFile && it.name.endsWith(filesExt) && (additionalFileCheck == null || !additionalFileCheck(it))) {
                callback(readFile(it), it)
            }
        }
    }

    fun run(additionalFileCheck: ((File) -> Boolean)? = null, callback: (T, File) -> Unit) {
        walkDirectory(additionalFileCheck) { content: T, file: File -> callback(content, file) }
    }
}