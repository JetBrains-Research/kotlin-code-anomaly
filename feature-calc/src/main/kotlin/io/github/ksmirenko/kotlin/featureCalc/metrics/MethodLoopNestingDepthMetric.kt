package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodLoopNestingDepthMetric : Metric(
        id = FeatureRecord.Type.MethodLoopNestingDepth,
        csvName = "loopNestingDepth",
        description = "Loop nesting depth"
) {

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var curLoopDepth = 0
        private var maxLoopDepth = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtLoopExpression -> visitKtLoopExpression(element)

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                curLoopDepth = 0
                maxLoopDepth = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, maxLoopDepth)
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
