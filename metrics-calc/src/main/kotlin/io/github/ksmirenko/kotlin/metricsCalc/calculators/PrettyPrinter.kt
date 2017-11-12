package io.github.ksmirenko.kotlin.metricsCalc.calculators

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class PrettyPrinter() : MetricsCalculator(null) {
    private val baseVisitor = PrettyPrintingVisitor()

    override fun writeCsvHeader() {
        // do nothing
    }

    override fun calculate(psiFile: PsiFile) {
        psiFile.accept(baseVisitor)
    }

    inner class PrettyPrintingVisitor : JavaRecursiveElementVisitor() {
        private var level = 1

        override fun visitElement(element: PsiElement?) {
            (1..level).forEach { print("\t") }
            println(element)

            level += 1
            super.visitElement(element)
            level -= 1
        }

        override fun visitFile(file: PsiFile?) {
            level = 1
            super.visitFile(file)
        }
    }
}
