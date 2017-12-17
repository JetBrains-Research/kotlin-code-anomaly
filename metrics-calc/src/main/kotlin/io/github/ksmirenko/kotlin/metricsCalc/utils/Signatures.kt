package io.github.ksmirenko.kotlin.metricsCalc.utils

import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter

fun KtNamedFunction.buildSimpleSignature(): String {
    val fqName = this.fqName
    return fqName?.asString()
            ?: this.containingFile.name + ":" + this.nameAsSafeName.asString()
}

fun KtNamedFunction.buildSignatureWithParameters(): String {
    val fqName = this.fqName
    val returnedName = fqName?.asString()
            ?: this.containingFile.name + ":" + this.nameAsSafeName.asString()
    val parameterString = this.valueParameters.joinToString(
            separator = ", ",
            prefix = "(",
            postfix = ")",
            transform = { it: KtParameter ->
                val modifiers = it.modifierList?.text
                val modifierString = if (modifiers != null) (modifiers + " ") else ""
                "$modifierString${it.name}: ${it.typeReference?.text ?: "<unknown>"}"
            }
    )
    return returnedName + parameterString
}
