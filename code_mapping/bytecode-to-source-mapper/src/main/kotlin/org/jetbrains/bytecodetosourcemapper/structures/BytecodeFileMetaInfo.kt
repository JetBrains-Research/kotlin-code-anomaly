package org.jetbrains.bytecodetosourcemapper.structures

typealias LineNumberBounds = MutableMap<String, Pair<Int, Int>>

data class BytecodeFileMetaInfo(
        val packageName: String,
        val sourceFileName: String,
        val lineNumberBounds: LineNumberBounds
)