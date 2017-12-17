package io.github.ksmirenko.kotlin.metricsCalc.utils

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.PsiGenerator
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SignatureWithParametersTest {
    private val testFile = "metrics-calc/src/main/kotlin/testSrc/FunSignatures.kt"
    private val expectedSignatures = arrayOf(
            "testSrc.FunSignatures.normal(foo: String)",
            "testSrc.FunSignatures.oneLiner()",
            "testSrc.FunSignatures.normal(foo: String, bar: Double)",
            "FunSignatures.kt:namedCompanion(in: String)",
            "FunSignatures.kt:anotherNamedCompanion(size: Int)",
            "testSrc.FunSignatures.Companion.unnamedCompanion()",
            "testSrc.FunSignaturesGeneric.outT()",
            "testSrc.FunSignaturesGeneric.inT(tValue: T)",
            "testSrc.FunSignaturesGeneric.inTV(tValue: T, vValue: V)",
            "testSrc.FunSignaturesSneaky.withSpaces(param: String)",
            "testSrc.FunSignaturesSneaky.withLineFeed(param: String)",
            "testSrc.FunSignaturesSneaky.withDefaultValue(foo: String)",
            "testSrc.FunSignaturesSneaky.withModifiers(foo: String, bar: Boolean)",
            "testSrc.FunSignaturesSneaky.withModifiers(vararg foo: String, bar: Boolean)",
            "testSrc.FunSignaturesSneaky.withModifiers(foo: String, vararg bar: Boolean)",
            "testSrc.FunSignaturesSneaky.infixFun(x: Int)",
            "testSrc.FunSignaturesSneaky.extensionInClass(foo: String)",
            "testSrc.FunSignaturesSneaky.Companion.extensionInCompanion(foo: String)",
            "testSrc.topLevel(foo: Int)",
            "testSrc.extensionAtTopLevel(foo: String)"
    )

    private val visitor = Visitor()
    private val actualSignaturesList = ArrayList<String>()

    @Test
    fun testSimpleSignatures() {
        val psiFile = PsiGenerator.generate(File(testFile))
        psiFile.accept(visitor)
        val actualSignatures = actualSignaturesList.toArray()

        assertTrue(expectedSignatures.size == actualSignatures.size)
        for ((expected, actual) in expectedSignatures.zip(actualSignatures)) {
            assertEquals(expected, actual)
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
            val signature = function.buildSignatureWithParameters()
            actualSignaturesList.add(signature)
        }
    }
}
