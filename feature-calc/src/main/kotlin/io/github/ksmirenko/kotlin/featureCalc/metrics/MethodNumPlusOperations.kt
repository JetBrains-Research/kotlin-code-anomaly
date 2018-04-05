package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

class MethodNumPlusOperations : Metric(
        csvName = "numPlusOperations",
        description = "Число операций сложения и конкатенации"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var plusOperationsCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtOperationReferenceExpression -> {
                    if (element.text == "+") {
                        plusOperationsCount++
                    }
                    super.visitElement(element)
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                plusOperationsCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, plusOperationsCount)
            }
        }
    }
}
