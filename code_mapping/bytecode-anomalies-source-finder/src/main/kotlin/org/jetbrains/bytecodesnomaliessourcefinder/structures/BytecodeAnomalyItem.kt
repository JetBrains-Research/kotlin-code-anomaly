package org.jetbrains.bytecodesnomaliessourcefinder.structures

data class BytecodeAnomalyItem(
        val bytecodeFile: String?,
        val bytecodeJsonFile: String?,
        val anomalyBytecodeValue: Float?
)