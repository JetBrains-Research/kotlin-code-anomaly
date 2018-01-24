package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

class MethodNumTypeCastExpressionsMetric : Metric() {
    override val headerName = "numTypecastExpr"
    override val description = "Number of typecast expressions"

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var typecastExprCount = 0
        private var methodNestingDepth = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                is KtOperationReferenceExpression -> {
                    if (element.text == "as" || element.text == "is") {
                        typecastExprCount += 1
                    }
                    super.visitElement(element)
                }
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                typecastExprCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(MetricRecord(MetricRecord.Type.MethodNumTypeCastExpr, funName, typecastExprCount))
            }
        }
    }
}
