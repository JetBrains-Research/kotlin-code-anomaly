package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.*

class MethodNumLoopsMetric : Metric() {
    override val headerName = "numLoopStatements"
    override val description = "Number of loop statements"

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var loopCount = 0
        private var methodNestingDepth = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                is KtDoWhileExpression -> {
                    loopCount += 1
                    super.visitElement(element)
                }
                is KtForExpression -> {
                    loopCount += 1
                    super.visitElement(element)
                }
                is KtWhileExpression -> {
                    loopCount += 1
                    super.visitElement(element)
                }
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                loopCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(MetricRecord(MetricRecord.Type.MethodNumLoops, funName, loopCount))
            }
        }
    }
}
