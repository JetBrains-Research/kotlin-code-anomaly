package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodAstHeightMetric : Metric() {
    override val headerName = "astHeight"
    override val description = "AST maximum height"

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var curAstHeight = 1
        private var maxAstHeight = 1

        override fun visitElement(element: PsiElement?) {
            if (curAstHeight > maxAstHeight) {
                maxAstHeight = curAstHeight
            }

            curAstHeight += 1
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                else -> super.visitElement(element)
            }
            curAstHeight -= 1
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                curAstHeight = 1
                maxAstHeight = 1
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(MetricRecord(MetricRecord.Type.MethodASTHeight, funName, maxAstHeight))
            }
        }
    }
}
