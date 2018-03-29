package io.github.ksmirenko.kotlin.featureCalc.records

data class FeatureRecord(
        val type: Type,
        val entityIdentifier: String,
        val value: Double
) {
    constructor(type: Type, entityIdentifier: String, value: Int) : this(type, entityIdentifier, value.toDouble())

    fun serialize(breakLine: Boolean = false): String {
        return "${type.ordinal},$entityIdentifier,$value${if (breakLine) "\n" else ""}"
    }

    companion object {
        fun deserialize(string: String): FeatureRecord? {
            val splitString = string.split(',', limit = 3)
            if (splitString.size < 3) {
                return null
            }
            val featureType = splitString[0].toIntOrNull()?.let { Type.values()[it] } ?: return null
            val entityId = splitString[1]
            val value = splitString[2].toDoubleOrNull() ?: return null
            return FeatureRecord(featureType, entityId, value)
        }
    }

    enum class Type {
        FileLoC, FileSLoC, FileASTHeight, FileASTNodeCount,
        MethodLoC, MethodSLoC, MethodASTHeight, MethodASTNodeCount,
        MethodLoopNestingDepth, MethodCyclomaticComplexity,
        MethodRelativeLoc, MethodDesignComplexity, MethodNumTypeCastExpr, MethodNumMethodCalls,
        MethodNumStatementExpressions, MethodNumExpressions, MethodNumValueParameters, MethodNumReturns, MethodNumLoops,
        MethodNumTypeParameters, MethodMaxNumWhenEntries, MethodNumIfExpressions,
        MethodNumAssignStatements, MethodNumNestedFunctions, MethodNumNestedClasses,
        MethodNumDeclarations, MethodNumBlocks, MethodNumTryExpressions, MethodNumCatchClauses,
        MethodNumFinallySections, MethodIsVoid, MethodNumForceUnwraps,
        MethodIsSuspend, MethodNumLambdas, MethodNumAnnotations,
        MethodTotalNumKeywords, MethodNumDistinctKeywords
    }
}
