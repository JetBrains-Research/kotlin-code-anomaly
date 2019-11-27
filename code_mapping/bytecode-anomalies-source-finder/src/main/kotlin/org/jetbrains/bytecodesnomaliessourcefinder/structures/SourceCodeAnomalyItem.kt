package org.jetbrains.bytecodesnomaliessourcefinder.structures

data class SourceCodeAnomalyItem(
        val sourceFile: String?,
        val psiFile: String?,
        val anomalyPsiValue: Float?
)