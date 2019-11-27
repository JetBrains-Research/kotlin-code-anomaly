package org.jetbrains.bytecodetosourcemapper.mapping

import org.apache.bcel.classfile.ClassParser
import org.apache.bcel.classfile.Method
import org.jetbrains.bytecodetosourcemapper.io.DirectoryWalker
import org.jetbrains.bytecodetosourcemapper.structures.BytecodeFileMetaInfo
import org.jetbrains.bytecodetosourcemapper.structures.LineNumberBounds
import java.io.File

class BytecodeMetaInfoExtractor {
    companion object {
        private const val BYTECODE_FILE_EXT = "class"

        fun walkClassesDirectory(classesDirectory: String, callback: (file: File) -> Unit) {
            DirectoryWalker(classesDirectory).run {
                if (it.isFile && it.extension == BYTECODE_FILE_EXT) {
                    callback(it)
                }
            }
        }
    }

    private fun calcLineNumberBounds(methods: Array<Method>): LineNumberBounds {
        val lineNumberBounds: LineNumberBounds = mutableMapOf()

        methods.forEach {
            if (it.lineNumberTable == null) {
                return@forEach
            }

            val lineNumberTable  = it.lineNumberTable.lineNumberTable
            val bottomBound = lineNumberTable.minBy { it.lineNumber }!!.lineNumber
            val topBound = lineNumberTable.maxBy { it.lineNumber }!!.lineNumber

            lineNumberBounds[it.name] = Pair(bottomBound, topBound)
        }

        return lineNumberBounds
    }

    fun extract(file: File): BytecodeFileMetaInfo {
        val classParsed = ClassParser(file.absolutePath).parse()
        val packageName = classParsed.packageName
        val sourceFileName = classParsed.sourceFileName
        val lineNumberBounds = calcLineNumberBounds(classParsed.methods)

        return BytecodeFileMetaInfo(packageName, sourceFileName, lineNumberBounds)
    }
}