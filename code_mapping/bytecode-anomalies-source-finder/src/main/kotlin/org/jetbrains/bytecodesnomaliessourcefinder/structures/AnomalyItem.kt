package org.jetbrains.bytecodesnomaliessourcefinder.structures

data class AnomalyItem(
        val jsonPath: String?,
        val classPath: String?,
        val anomalyBytecodeValue: Float?,
        val anomalyPsiValue: Float?,
        val sourceFile: String?,
        val psiFile: String?
)