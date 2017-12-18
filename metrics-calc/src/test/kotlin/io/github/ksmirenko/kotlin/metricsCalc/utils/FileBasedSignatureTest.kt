package io.github.ksmirenko.kotlin.metricsCalc.utils

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.PsiGenerator
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.junit.Assert
import org.junit.Test
import java.io.File

class FileBasedSignatureTest {
    private val testFile = "metrics-calc/src/main/kotlin/testSrc/FunSignatures.kt"
    private val expectedSignatures = arrayOf(
            "$testFile:FunSignatures.normal(foo: String)",
            "$testFile:FunSignatures.oneLiner()",
            "$testFile:FunSignatures.normal(foo: String, bar: Double)",
            "$testFile:<no name provided>.funInAnonObject(in: String)",
            "$testFile:<no name provided>.anotherFunInAnonObject(size: Int)",
            "$testFile:Companion.unnamedCompanion()",
            "$testFile:FunSignaturesGeneric.outT()",
            "$testFile:FunSignaturesGeneric.inT(tValue: T)",
            "$testFile:FunSignaturesGeneric.inTV(tValue: T, vValue: V)",
            "$testFile:FunSignaturesSneaky.withSpaces(param: String)",
            "$testFile:FunSignaturesSneaky.withLineFeed(param: String)",
            "$testFile:FunSignaturesSneaky.withDefaultValue(foo: String)",
            "$testFile:FunSignaturesSneaky.withModifiers(foo: String, bar: Boolean)",
            "$testFile:FunSignaturesSneaky.withModifiers(vararg foo: String, bar: Boolean)",
            "$testFile:FunSignaturesSneaky.withModifiers(foo: String, vararg bar: Boolean)",
            "$testFile:FunSignaturesSneaky.infixFun(x: Int)",
            "$testFile:FunSignaturesSneaky.extensionInClass(foo: String)",
            "$testFile:Companion.extensionInCompanion(foo: String)",
            "$testFile:.topLevel(foo: Int)",
            "$testFile:.extensionAtTopLevel(foo: String)"
    )

    private val visitor = Visitor()
    private val actualSignaturesList = ArrayList<String>()

    @Test
    fun testSimpleSignatures() {
        val psiFile = PsiGenerator.generate(File(testFile))
        psiFile.accept(visitor)
        val actualSignatures = actualSignaturesList.toArray()

        Assert.assertTrue(expectedSignatures.size == actualSignatures.size)
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
            val signature = function.buildFileBasedSignature(testFile)
            actualSignaturesList.add(signature)
        }
    }
}
