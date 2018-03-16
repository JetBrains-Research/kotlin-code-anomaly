package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumExpressionsFeature : Feature(
        id = FeatureRecord.Type.MethodNumExpressions,
        csvName = "numExpressions",
        description = "Number of expressions"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
        private var exprCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                is KtClassOrObject -> {
                }  // skip nested classes

                is KtExpression -> {
                    exprCount += 1
                    super.visitElement(element)
                }
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (isInsideFunction) {
                // skip nested functions
                return
            }
            isInsideFunction = true

            exprCount = 0
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, exprCount)
            isInsideFunction = false
        }
    }
}
