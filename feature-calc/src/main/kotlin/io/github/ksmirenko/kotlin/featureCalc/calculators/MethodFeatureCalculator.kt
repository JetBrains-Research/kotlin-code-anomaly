package io.github.ksmirenko.kotlin.featureCalc.calculators

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.github.ksmirenko.kotlin.featureCalc.metrics.*
import io.github.ksmirenko.kotlin.featureCalc.utils.buildFileBasedSignature
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodFeatureCalculator(outFileName: String?) : FeatureCalculator(outFileName) {
    private val features = listOf(
            MethodSlocMetric()
            , MethodRelativeLocMetric()

            , MethodNodeCountMetric()
            , MethodCstHeightMetric()

            , MethodMaxLoopNestingDepthMetric()
            , MethodCyclomaticComplexityMetric()
            , MethodDesignComplexityMetric()
            // 7

            , MethodNumTypeCastExpressionsMetric()
            , MethodNumMethodCallsMetric()
            , MethodNumStatementExpressionsMetric()
            , MethodNumExpressionsMetric()
            , MethodNumReturnPointsMetric()
            , MethodNumValueParametersMetric()
            , MethodNumLoopsMetric()
            // 14

            , MethodNumTypeParametersMetric()
            , MethodMaxNumWhenEntriesMetric()
            , MethodNumIfExpressionsMetric()
            , MethodNumAssignStatementsMetric()
            , MethodNumNestedFunctionsMetric()
            , MethodNumNestedClassesMetric()
            // 20

            , MethodNumDeclarationsMetric()
            , MethodNumBlocksMetric()
            , MethodNumTryExpressionsMetric()
            , MethodNumCatchClausesMetric()
            , MethodNumFinallySectionsMetric()
            , MethodIsVoidMetric()
            , MethodNumForceUnwrapsMetric()
            // 27

            , MethodIsSuspendMetric()
            , MethodNumLambdasMetric()
            , MethodNumAnnotationsMetric()
            , MethodTotalNumKeywordsMetric()
            , MethodNumDistinctKeywordsMetric()
            // 32

            , MethodMaxNumBlockChildrenMetric()
            , MethodAvgNumBlockChildrenMetric()
            , MethodNumEmptyBlocksMetric()
            , MethodNumSingleChildBlocksMetric()

            , MethodNumConstants()
            , MethodNumStringTemplates()
            , MethodNumStringLiterals()
            , MethodNumBlockStringTemplates()
            , MethodNumReferences()
            , MethodNumOperations()
            , MethodNumThrows()
            , MethodNumSafeQualifiedExpressions()
            , MethodNumClassLiterals()
            , MethodNumCollectionLiterals()
            // 46
    )

    private val csvDelimiter = "\t"
    private val baseVisitor = KtFunctionSeekingVisitor()

    override fun writeCsvHeader() {
        features
                .joinToString(separator = csvDelimiter, prefix = "id${csvDelimiter}methodName$csvDelimiter",
                        postfix = "\n") {
                    it.csvName
                }.let { writer.write(it) }
    }

    override fun calculate(psiFile: PsiFile, path: String?) {
        baseVisitor.currentFilePath = path
        psiFile.accept(baseVisitor)
    }

    fun printFeatureDescriptions() {
        features.sortedBy { it.csvName }
                .joinToString(separator = "\\\\\n", transform = { "${it.csvName} & ${it.description}" })
                .let { println(it) }
    }

    private inner class KtFunctionSeekingVisitor : JavaRecursiveElementVisitor() {
        var currentFilePath: String? = null
        private var funId = 1L

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            val funName = function.fqName.toString()
            val signature = function.buildFileBasedSignature(currentFilePath)
            val recordStringBuilder = StringBuilder(funId.toString() + csvDelimiter + signature)
            for (feature in features) {
                function.accept(feature.visitor)

                recordStringBuilder.append(csvDelimiter)
                val record = feature.lastRecord ?: continue
                if (record.entityIdentifier != funName) {
                    throw IllegalStateException("Fun name from record (${record.entityIdentifier}) " +
                            "doesn't match current fun name ($funName)!")
                }
                recordStringBuilder.append(record.value)
            }

            writer.write(recordStringBuilder.append('\n').toString())
            writer.flush()
            funId++
        }
    }
}
