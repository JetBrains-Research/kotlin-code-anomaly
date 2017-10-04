package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodLoopNestingDepthMetric : Metric() {
    override val description = "Loop nesting depth"

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var curLoopDepth = 0
        private var maxLoopDepth = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                is KtLoopExpression -> visitKtLoopExpression(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                curLoopDepth = 0
                maxLoopDepth = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(MetricRecord(MetricRecord.Type.MethodLoopNestingDepth, funName, maxLoopDepth))
            }
        }

        private fun visitKtLoopExpression(loopExpression: KtLoopExpression) {
            curLoopDepth += 1
            if (curLoopDepth > maxLoopDepth) {
                maxLoopDepth = curLoopDepth
            }
            super.visitElement(loopExpression)
            curLoopDepth -= 1
        }
    }
}