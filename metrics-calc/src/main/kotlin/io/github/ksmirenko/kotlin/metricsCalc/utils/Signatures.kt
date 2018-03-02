package io.github.ksmirenko.kotlin.metricsCalc.utils

import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

fun KtNamedFunction.buildSimpleSignature(): String {
    val fqName = this.fqName
    return fqName?.asString()
            ?: this.containingFile.name+":"+this.nameAsSafeName.asString()
}

fun KtNamedFunction.buildSignatureWithParameters(): String {
    val fqName = this.fqName
    val returnedName = fqName?.asString()
            ?: this.containingFile.name+":"+this.nameAsSafeName.asString()
    val parameters = this.buildParameterString()
    return returnedName + parameters
}

fun KtNamedFunction.buildFileBasedSignature(pathToFile: String?): String {
    val path = pathToFile ?: this.containingFile.name
    val parent = this.containingClassOrObject?.nameAsSafeName?.asString() ?: ""
    val name = this.nameAsSafeName.asString()
    val parameters = this.buildParameterString()
    return "$path:$parent.$name$parameters"
}

private fun KtNamedFunction.buildParameterString() = this.valueParameters.joinToString(
        separator = ", ",
        prefix = "(",
        postfix = ")",
        transform = { it: KtParameter ->
            val modifiers = it.modifierList?.text
            val modifierString = if (modifiers != null) "$modifiers " else ""
            "$modifierString${it.name}: ${it.typeReference?.text ?: "<unknown>"}"
                    .replace(Regex("\\s+"), " ")
        }
)
