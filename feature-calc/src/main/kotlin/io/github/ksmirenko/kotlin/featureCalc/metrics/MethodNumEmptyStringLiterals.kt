package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

class MethodNumEmptyStringLiterals : Metric(
        csvName = "numEmptyStringLiterals",
        description = "Число пустых строковых литералов"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var emptyStringLiteralCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtStringTemplateExpression -> {
                    if (element.text == "\"\"") {
                        emptyStringLiteralCount++
                    }
                    super.visitElement(element)
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                emptyStringLiteralCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, emptyStringLiteralCount)
            }
        }
    }
}
