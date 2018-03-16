package io.github.ksmirenko.kotlin.featureCalc.calculators

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.github.ksmirenko.kotlin.featureCalc.features.*
import io.github.ksmirenko.kotlin.featureCalc.utils.buildFileBasedSignature
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodFeatureCalculator(outFileName: String) : FeatureCalculator(outFileName) {
    private val features = listOf(
            MethodSlocFeature()
            , MethodRelativeLocFeature()

            , MethodNodeCountFeature()
            , MethodAstHeightFeature()

            , MethodLoopNestingDepthFeature()
            , MethodCyclomaticComplexityFeature()
            , MethodDesignComplexityFeature()

            , MethodNumTypeCastExpressionsFeature()
            , MethodNumMethodCallsFeature()
            , MethodNumStatementExpressionsFeature()
            , MethodNumExpressionsFeature()
            , MethodNumReturnPointsFeature()
            , MethodNumValueParametersFeature()
            , MethodNumLoopsFeature()

            , MethodNumTypeParametersFeature()
            , MethodMaxNumWhenEntriesFeature()
            , MethodNumIfExpressionsFeature()
            , MethodNumAssignStatementsFeature()
            , MethodNumNestedFunctionsFeature()
            , MethodNumNestedClassesFeature()
    )

    private val csvDelimiter = "\t"
    private val baseVisitor = KtFunctionSeekingVisitor()

    override fun writeCsvHeader() {
        features.joinToString(separator = csvDelimiter, prefix = "methodName$csvDelimiter", postfix = "\n") {
            it.csvName
        }.let { writer.write(it) }
    }

    override fun calculate(psiFile: PsiFile, path: String?) {
        baseVisitor.currentFilePath = path
        psiFile.accept(baseVisitor)
    }


    private inner class KtFunctionSeekingVisitor : JavaRecursiveElementVisitor() {
        var currentFilePath: String? = null

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            val funName = function.fqName.toString()
            val signature = function.buildFileBasedSignature(currentFilePath)
            val recordStringBuilder = StringBuilder(signature)
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
        }
    }
}
