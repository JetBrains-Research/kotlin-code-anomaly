package org.jetbrains.bytecodetosourcemapper.structures

import java.io.File

data class BytecodeToSourceMapElement(
    val file: File,
    val lineNumbers: LineNumberBounds
)