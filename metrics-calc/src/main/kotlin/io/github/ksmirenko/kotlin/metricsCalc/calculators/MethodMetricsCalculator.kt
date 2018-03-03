package io.github.ksmirenko.kotlin.metricsCalc.calculators

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.github.ksmirenko.kotlin.metricsCalc.metrics.*
import io.github.ksmirenko.kotlin.metricsCalc.utils.buildFileBasedSignature
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodMetricsCalculator(outFileName: String) : MetricsCalculator(outFileName) {
    private val metrics = listOf(
            MethodSlocMetric()
            , MethodRelativeLocMetric()

            , MethodNodeCountMetric()
            , MethodAstHeightMetric()

            , MethodLoopNestingDepthMetric()
            , MethodCyclomaticComplexityMetric()
            , MethodDesignComplexityMetric()

            , MethodNumTypeCastExpressionsMetric()
            , MethodNumMethodCallsMetric()
            , MethodNumStatementExpressionsMetric()
            , MethodNumExpressionsMetric()
            , MethodNumReturnPointsMetric()
            , MethodNumValueParametersMetric()
            , MethodNumLoopsMetric()
    )

    private val csvDelimiter = "\t"
    private val baseVisitor = KtFunctionSeekingVisitor()

    override fun writeCsvHeader() {
        metrics.joinToString(separator = csvDelimiter, prefix = "methodName$csvDelimiter", postfix = "\n") {
            it.headerName
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
            for (metric in metrics) {
                function.accept(metric.visitor)

                recordStringBuilder.append(csvDelimiter)
                val record = metric.lastRecord ?: continue
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
