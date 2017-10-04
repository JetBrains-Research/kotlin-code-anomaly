package io.github.ksmirenko.kotlin.metricsCalc.calculators

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.sixrr.stockmetrics.utils.LineUtil

class FileMetricsCalculator(outFileName: String): MetricsCalculator(outFileName) {
    // the order of metrics in a record is: LoC, SLoC, AST node count, AST max height
    private val baseVisitor = FileMetricsVisitor()

    override fun calculate(psiFile: PsiFile) {
        psiFile.accept(baseVisitor)
    }

    inner class FileMetricsVisitor : JavaRecursiveElementVisitor() {
        private var nodeCount = 0
        private var level = 1
        private var maxLevel = 0
        private var commentLines = 0

        override fun visitElement(element: PsiElement?) {
            if (element == null) {
                return
            }

            nodeCount++
            if (level > maxLevel) {
                maxLevel = level
            }

            level += 1
            super.visitElement(element)
            level -= 1
        }

        override fun visitFile(file: PsiFile?) {
            if (file == null) {
                return
            }
            nodeCount = 0
            level = 1
            maxLevel = 0
            commentLines = 0

            super.visitFile(file)
            val lines = LineUtil.countLines(file)

            val record = ("${file.name},$lines,${lines - commentLines},$nodeCount,$maxLevel\n")
            writer.write(record)
        }

        override fun visitComment(comment: PsiComment?) {
            if (comment == null) {
                return
            }

            super.visitComment(comment)
            commentLines += LineUtil.countCommentOnlyLines(comment)
        }
    }
}