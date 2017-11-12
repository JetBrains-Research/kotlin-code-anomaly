package io.github.ksmirenko.kotlin.metricsCalc.utils

import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.modalityModifier

val KtNamedFunction.isAbstract: Boolean
    get() = this.modalityModifier()?.text == "abstract"