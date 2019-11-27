package org.jetbrains.bytecodesnomaliessourcefinder.io

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

class JsonFilesReader<T>(private val dirPath: String, private val filesExt: String, private val entityType: TypeReference<*>) {
    companion object {
        fun <TEntity>readFile(file: File, entityType: TypeReference<*>): TEntity {
            return jacksonObjectMapper().readValue(file.readText(), entityType)
        }
    }

    private fun walkDirectory(withoutFirstSelect: Boolean = false, callback: (T, File) -> Unit) {
        val dir = File(dirPath)
        dir.walkTopDown().forEach {
            if (it.isFile && it.extension == filesExt) {
                if (withoutFirstSelect) {
                    callback(readFile(it, entityType), it)
                } else {
                    callback(readFile<ArrayList<T>>(it, entityType)[0], it)
                }
            }
        }
    }

    fun run(withoutFirstSelect: Boolean = false, callback: (T, File) -> Unit) {
        walkDirectory(withoutFirstSelect) { content: T, file: File -> callback(content, file) }
    }
}