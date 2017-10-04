package io.github.ksmirenko.kotlin.metricsCalc.records

data class MetricRecord(
        val type: Type,
        val entityIdentifier: String,
        val value: Int
) {
    fun serialize(breakLine: Boolean = false): String {
        return "${type.ordinal},$entityIdentifier,$value${if (breakLine) "\n" else ""}"
    }

    companion object {
        fun deserialize(string: String): MetricRecord? {
            val splitString = string.split(',', limit = 3)
            if (splitString.size < 3) {
                return null
            }
            val metricType = splitString[0].toIntOrNull()?.let { Type.values()[it] } ?: return null
            val entityId = splitString[1]
            val value = splitString[2].toIntOrNull() ?: return null
            return MetricRecord(metricType, entityId, value)
        }
    }

    enum class Type {
        FileLoC, FileSLoC, FileASTHeight, FileASTNodeCount,
        MethodLoC, MethodSLoC, MethodASTHeight, MethodASTNodeCount,
        MethodLoopNestingDepth, MethodCyclomaticComplexity
    }
}
