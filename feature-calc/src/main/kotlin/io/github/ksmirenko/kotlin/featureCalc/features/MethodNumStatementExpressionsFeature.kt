package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtStatementExpression

class MethodNumStatementExpressionsFeature : Feature(
        id = FeatureRecord.Type.MethodNumStatementExpressions,
        csvName = "numStatementExpressions",
        description = "Number of Kotlin StatementExpressions"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var stmtCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitOuterFunction(element)

                is KtStatementExpression -> {
                    if (element !is KtBlockExpression) {
                        stmtCount += 1
                    }
                    super.visitElement(element)
                }
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                stmtCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, stmtCount)
            }
        }
    }
}
