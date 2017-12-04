package io.github.ksmirenko.kotlin.metricsCalc.utils

import org.jetbrains.kotlin.psi.KtNamedFunction

fun KtNamedFunction.buildSimpleSignature(): String {
    val fqName = this.fqName
    return fqName?.asString()
            ?: this.containingFile.name + ":" + this.nameAsSafeName.asString()
}
