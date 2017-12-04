package io.github.ksmirenko.kotlin.metricsCalc.utils

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.PsiGenerator
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SignaturesTest {
    private val testFile = "metrics-calc/src/main/kotlin/testSrc/FunSignatures.kt"
    private val expectedSimpleSignatures = arrayOf(
            "testSrc.FunSignatures.normal",
            "testSrc.FunSignatures.oneLiner",
            "FunSignatures.kt:companion",
            "FunSignatures.kt:anotherCompanion"
    )

    private val visitor = Visitor()
    private val actualSignaturesList = ArrayList<String>()

    @Test
    fun testSimpleSignatures() {
        val psiFile = PsiGenerator.generate(File(testFile))
        psiFile.accept(visitor)

        val actualSignatures = actualSignaturesList.toArray()
        assertTrue(expectedSimpleSignatures contentEquals actualSignatures)
    }

    private inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            val signature = function.buildSimpleSignature()
            actualSignaturesList.add(signature)
        }
    }
}
