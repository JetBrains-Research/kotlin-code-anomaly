package io.github.ksmirenko.kotlin.metricsCalc.utils

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.PsiGenerator
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class SimpleSignatureTest {
    private val testFile = "src/main/kotlin/testSrc/FunSignatures.kt"
    private val expectedSignatures = arrayOf(
            "testSrc.FunSignatures.normal",
            "testSrc.FunSignatures.oneLiner",
            "testSrc.FunSignatures.normal",
            "FunSignatures.kt:funInAnonObject",
            "FunSignatures.kt:anotherFunInAnonObject",
            "testSrc.FunSignatures.Companion.unnamedCompanion",
            "testSrc.FunSignaturesGeneric.outT",
            "testSrc.FunSignaturesGeneric.inT",
            "testSrc.FunSignaturesGeneric.inTV",
            "testSrc.FunSignaturesSneaky.withSpaces",
            "testSrc.FunSignaturesSneaky.withLineFeed",
            "testSrc.FunSignaturesSneaky.withDefaultValue",
            "testSrc.FunSignaturesSneaky.withModifiers",
            "testSrc.FunSignaturesSneaky.withModifiers",
            "testSrc.FunSignaturesSneaky.withModifiers",
            "testSrc.FunSignaturesSneaky.withArgAnnotationsAndTabs",
            "testSrc.FunSignaturesSneaky.infixFun",
            "testSrc.FunSignaturesSneaky.extensionInClass",
            "testSrc.FunSignaturesSneaky.Companion.extensionInCompanion",
            "testSrc.topLevel",
            "testSrc.extensionAtTopLevel"
    )

    private val visitor = Visitor()
    private val actualSignaturesList = ArrayList<String>()

    @Test
    fun testSimpleSignatures() {
        val path = Paths.get("").toAbsolutePath().toString()
        println("Working directory: $path")

        val psiFile = PsiGenerator.generate(File(testFile))
        psiFile.accept(visitor)
        val actualSignatures = actualSignaturesList.toArray()

        assertTrue(expectedSignatures.size == actualSignatures.size)
        for ((expected, actual) in expectedSignatures.zip(actualSignatures)) {
            Assert.assertEquals(expected, actual)
        }
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
