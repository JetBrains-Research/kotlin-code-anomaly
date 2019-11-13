package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.sixrr.stockmetrics.utils.LineUtil
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodSlocMetric : Metric(
        csvName = "sloc",
        description = "Число строк кода"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var commentLines = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                else -> super.visitElement(element)
            }
        }

        override fun visitComment(comment: PsiComment?) {
            if (comment == null) {
                return
            }

            super.visitComment(comment)
            commentLines += LineUtil.countCommentOnlyLines(comment)
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                commentLines = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val lines = LineUtil.countLines(function)
                val funName = function.fqName.toString()
                appendRecord(funName, lines - commentLines)
            }
        }
    }
}
