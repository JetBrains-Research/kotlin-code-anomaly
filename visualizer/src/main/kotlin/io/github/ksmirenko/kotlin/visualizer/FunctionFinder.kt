package io.github.ksmirenko.kotlin.visualizer

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.utils.buildFileBasedSignature
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.io.File

class FunctionFinder(
        private val shouldPrintToConsole: Boolean = false
) : JavaRecursiveElementVisitor() {
    var foundFunction: String? = null

    private lateinit var expectedSignature: String
    private lateinit var currentFilePath: String

    fun reset(expectedSignature: String, currentFilePath: String) {
        this.expectedSignature = expectedSignature
        this.currentFilePath = currentFilePath
        foundFunction = null
    }

    override fun visitElement(element: PsiElement?) {
        when (element) {
            is KtNamedFunction -> visitKtFunction(element)
            else -> super.visitElement(element)
        }
    }

    private fun visitKtFunction(function: KtNamedFunction) {
        val actualSignature = function.buildFileBasedSignature(currentFilePath)
        if (expectedSignature == actualSignature) {
            foundFunction = function.text
            if (shouldPrintToConsole) {
                println(foundFunction)
            }
        }
    }
}
