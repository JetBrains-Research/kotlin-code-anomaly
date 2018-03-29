package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.*

class MethodNumAssignStatementsFeature : Feature(
        id = FeatureRecord.Type.MethodNumAssignStatements,
        csvName = "numAssigns",
        description = "Number of assign statements, including assignments in property declarations"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
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
                is KtClassOrObject -> {
                }  // skip nested classes
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (isInsideFunction) {
                // FIXME: do not skip nested functions
                return
            }
            isInsideFunction = true

            numAssigns = 0
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, numAssigns)
            isInsideFunction = false
        }
    }
}
