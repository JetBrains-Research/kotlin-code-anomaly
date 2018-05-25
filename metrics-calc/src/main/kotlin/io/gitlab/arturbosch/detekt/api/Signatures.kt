package io.gitlab.arturbosch.detekt.api

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.asJava.namedUnwrappedElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.psi.psiUtil.startOffset

fun buildFullFunctionSignature(function: KtNamedFunction): String {
    val fqName = function.fqName
    val signature = if (fqName != null) {
        val packageAndName = function.fqName.toString()
        // TODO: fix for one-line functions without parentheses
        val paramsAndReturnType = buildFunctionSignature(function)
                .replaceBefore("fun", "")
                .replaceBefore('(', "")
        packageAndName //+ paramsAndReturnType
    } else {
        buildFunctionSignature(function)
                .replaceBefore("fun", "")
                .replaceFirst(Regex("fun\\s*"), "")
    }
    return signature.replace(Regex("\n\\s*"), " ")
}

internal fun PsiElement.searchName(): String {
    return this.namedUnwrappedElement?.name ?: "<UnknownName>"
}

internal fun PsiElement.searchClass(): String {
    val classElement = this.getNonStrictParentOfType(KtClassOrObject::class.java)
    var className = classElement?.name
    if (className != null && className == "Companion") {
        classElement?.parent?.getNonStrictParentOfType(KtClassOrObject::class.java)?.name?.let {
            className = it + ".$className"
        }
    }
    return className ?: this.containingFile.name
}

internal fun PsiElement.buildFullSignature(): String {
    val signature = this.searchSignature()
    val fullClassSignature = this.parents.filter { it is KtClassOrObject }
            .map { it.extractClassName() }
            .fold("") { sig, sig2 -> "$sig2${dotOrNot(sig, sig2)}$sig" }
    val filename = this.containingFile.name
    return (if (!fullClassSignature.startsWith(filename)) filename + "\$" else "") +
            if (fullClassSignature.isNotEmpty()) "$fullClassSignature\$$signature" else signature
}

private fun PsiElement.extractClassName() =
        this.getNonStrictParentOfType(KtClassOrObject::class.java)?.nameAsSafeName?.asString() ?: ""

private fun PsiElement.searchSignature(): String {
    return when (this) {
        is KtNamedFunction -> buildFunctionSignature(this)
        is KtClassOrObject -> buildClassSignature(this)
        is KtFile -> fileSignature()
        else -> this.text
    }.replace('\n', ' ').replace(Regex("\\s(\\s|\t)+"), " ")
}

private fun KtFile.fileSignature() = "${this.packageFqName.asString()}.${this.name}"

private fun dotOrNot(sig: String, sig2: String) = if (sig.isNotEmpty() && sig2.isNotEmpty()) "." else ""

private fun buildClassSignature(classOrObject: KtClassOrObject): String {
    var baseName = classOrObject.nameAsSafeName.asString()
    val typeParameters = classOrObject.typeParameters
    if (typeParameters.size > 0) {
        baseName += "<"
        baseName += typeParameters.joinToString(", ") { it.text }
        baseName += ">"
    }
    val extendedEntries = classOrObject.superTypeListEntries
    if (extendedEntries.isNotEmpty()) baseName += " : "
    extendedEntries.forEach { baseName += it.typeAsUserType?.referencedName ?: "" }
    return baseName
}

private fun buildFunctionSignature(element: KtNamedFunction): String {
    var methodStart = 0
    element.docComment?.let {
        methodStart = it.endOffset - it.startOffset
    }
    var methodEnd = element.endOffset - element.startOffset
    val typeReference = element.typeReference
    if (typeReference != null) {
        methodEnd = typeReference.endOffset - element.startOffset
    } else {
        element.valueParameterList?.let {
            methodEnd = it.endOffset - element.startOffset
        }
    }
    require(methodStart < methodEnd) {
        "Error building function signature with range $methodStart - $methodEnd for element: ${element.text}"
    }
    // edited by Kirill: for metrics-calc this is more useful
    return element.text.substring(methodStart, methodEnd)
//    return getTextSafe(
//            { element.nameAsSafeName.identifier },
//            { element.text.substring(methodStart, methodEnd) })
}

/*
 * When analyzing sub path 'testData' of the kotlin project, CompositeElement.getText() throws
 * a RuntimeException stating 'Underestimated text length' - #65.
 */
@Suppress("TooGenericExceptionCaught")
internal fun getTextSafe(defaultValue: () -> String, block: () -> String) = try {
    block()
} catch (e: RuntimeException) {
    val message = e.message
    if (message != null && message.contains("Underestimated text length")) {
        defaultValue() + "!<UnderestimatedTextLengthException>"
    } else {
        defaultValue()
    }
}