package io.github.ksmirenko.kotlin.featureCalc.calculators

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace

class PrettyPrinter : FeatureCalculator(null) {
    private val baseVisitor = PrettyPrintingVisitor()

    override fun writeCsvHeader() {
        // do nothing
    }

    override fun calculate(psiFile: PsiFile, path: String?) {
        psiFile.accept(baseVisitor)
    }

    inner class PrettyPrintingVisitor : JavaRecursiveElementVisitor() {
        private var level = 1

        override fun visitElement(element: PsiElement?) {
            if (element is PsiWhiteSpace) {
                return
            }

            (1..level).forEach { print("  ") }
            print(element)
            println("\t\t${element?.text
                    ?.replace("\n", "")
                    ?.replace("\\s+".toRegex(), " ")
                    ?.take(200)}"
            )

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
