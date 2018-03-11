package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

class MethodNumTypeCastExpressionsMetric : Metric(
        id = MetricRecord.Type.MethodNumTypeCastExpr,
        csvName = "numTypecastExpr",
        description = "Number of typecast expressions"
) {

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
        private var typecastExprCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtOperationReferenceExpression -> {
                    if (element.text == "as" || element.text == "is") {
                        typecastExprCount += 1
                    }
                    super.visitElement(element)
                }

                is KtNamedFunction -> visitKtFunction(element)
                is KtClassOrObject -> {
                }  // skip nested classes
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (isInsideFunction) {
                // skip nested functions
                return
            }
            isInsideFunction = true

            typecastExprCount = 0
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, typecastExprCount)
            isInsideFunction = false
        }
    }
}
