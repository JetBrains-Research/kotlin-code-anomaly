package io.github.ksmirenko.kotlin.featureCalc.utils

import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.modalityModifier

val KtNamedFunction.isAbstract: Boolean
    get() = this.modalityModifier()?.text == "abstract"

/*
object MethodUtils {
    fun calculateSignature(function: KtNamedFunction): String {
        val className = function.containingClassOrObject?.fqName?.asString() ?: ""
        val methodName = function.name
        val out = StringBuilder(50)
        out.append(className)
        out.append('.')
        out.append(methodName)
        out.append('(')
        val parameterList = function.getValueParameters().joinToString(separator = ",", prefix = "(", postfix = ")",
                transform = {
                    "${it.text}: ${it.getType()}"
                })
//        val parameters = parameterList.parameters
        for (i in parameters.indices) {
            if (i != 0) {
                out.append(',')
            }
            val parameterType = parameters[i].type
            val parameterTypeText = parameterType.presentableText
            out.append(parameterTypeText)
        }
        out.append(')')
        return out.toString()
    }
}
*/
