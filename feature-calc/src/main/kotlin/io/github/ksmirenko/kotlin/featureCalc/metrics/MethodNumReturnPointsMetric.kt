package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression

class MethodNumReturnPointsMetric : Metric(
        csvName = "numReturns",
        description = "Number of return points"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
        private var returnsCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtReturnExpression -> {
                    returnsCount += 1
                }
                is KtLambdaExpression -> {
                }

                is KtNamedFunction -> visitOuterFunction(element)
                is KtClassOrObject -> {
                }  // skip nested classes
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (isInsideFunction) {
                // skip nested functions
                return
            }
            isInsideFunction = true

            returnsCount = 0
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, returnsCount)
            isInsideFunction = false
        }
    }
}
