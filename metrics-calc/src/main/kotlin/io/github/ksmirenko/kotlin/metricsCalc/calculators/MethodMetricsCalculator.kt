package io.github.ksmirenko.kotlin.metricsCalc.calculators

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.github.ksmirenko.kotlin.metricsCalc.metrics.*
import io.gitlab.arturbosch.detekt.api.buildFullFunctionSignature
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodMetricsCalculator(outFileName: String) : MetricsCalculator(outFileName) {
    // note: the order of metrics is important
    private val metrics = listOf(
            MethodSlocMetric(),
            MethodNodeCountMetric(),
            MethodAstHeightMetric(),
            MethodLoopNestingDepthMetric(),
            MethodCyclomaticComplexityMetric()
    )

    private val csvDelimiter = "|"
    private val baseVisitor = KtFunctionSeekingVisitor()

    override fun writeCsvHeader() {
        metrics.joinToString(separator = csvDelimiter, prefix = "methodName$csvDelimiter", postfix = "\n") {
            it.headerName
        }.let { writer.write(it) }
    }

    override fun calculate(psiFile: PsiFile) {
        psiFile.accept(baseVisitor)
    }


    private inner class KtFunctionSeekingVisitor : JavaRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            val funName = function.fqName.toString()
            val signature = buildFullFunctionSignature(function)

            val recordStringBuilder = StringBuilder("\"$signature\"")
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
        }
    }
}
