package io.github.ksmirenko.kotlin.featureCalc.calculators

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.sixrr.stockmetrics.utils.LineUtil

class FileFeatureCalculator(outFileName: String) : FeatureCalculator(outFileName) {
    private val csvDelimiter = "\t"
    private val baseVisitor = FileFeatureVisitor()

    override fun writeCsvHeader() {
        listOf("fileName", "loc", "sloc", "nodeCount", "astMaxHeight")
                .joinToString(separator = csvDelimiter, postfix = "\n")
                .let { writer.write(it) }
    }

    override fun calculate(psiFile: PsiFile, path: String?) {
        psiFile.accept(baseVisitor)
    }

    inner class FileFeatureVisitor : JavaRecursiveElementVisitor() {
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
