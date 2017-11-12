package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.sixrr.stockmetrics.utils.LineUtil
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodRelativeLocMetric : Metric() {
    override val headerName = "relativeLoc"
    override val description = "Relative lines of code"

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                val funLinesCount = LineUtil.countLines(function)
                val funName = function.fqName.toString()
                // parent is assumed to be class, object or file
                val parentLinesCount = LineUtil.countLines(function.parent)
                appendRecord(MetricRecord(MetricRecord.Type.MethodRelativeLoc, funName,
                        1.0 * funLinesCount / parentLinesCount))
            }
            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--
        }
    }
}
