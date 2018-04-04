package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.*

class MethodNumAssignStatementsMetric : Metric(
        id = FeatureRecord.Type.MethodNumAssignStatements,
        csvName = "numAssigns",
        description = "Number of assign statements, including assignments in property declarations"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var numAssigns = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtProperty -> {
                    if (element.hasInitializer()) {
                        numAssigns += 1
                        super.visitElement(element)
                    }
                }
                is KtBinaryExpression -> {
                    val operation = (element.operationToken as? KtSingleValueToken)?.value
                    if (operation == "=") {
                        numAssigns += 1
                        super.visitElement(element)
                    }
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                numAssigns = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, numAssigns)
            }
        }
    }
}
