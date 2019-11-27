package org.jetbrains.bytecodesnomaliessourcefinder.structures

data class BytecodeToSourceItem(
        val file: String,
        private val lineNumbers: Map<String, Map<String, Int>>
)