@file:Suppress("unused", "UNUSED_PARAMETER", "ReplaceArrayOfWithLiteral")

package testSrc

class FunSignatures {

    fun normal(foo: String): Int {
        return foo.length
    }

    fun oneLiner() = 42

    // overload
    fun normal(foo: String, bar: Double): Int {
        return foo.length + bar.toInt()
    }

    val anonymousObject = object {
        fun funInAnonObject(`in`: String): String {
            return `in`
        }

        fun anotherFunInAnonObject(size: Int): Array<Int?> {
            return arrayOfNulls(size)
        }
    }

    companion object {
        fun unnamedCompanion() {
        }
    }
}

class FunSignaturesGeneric<T> {
    fun outT(): T? {
        return null
    }

    fun inT(tValue: T) {
    }

    fun <V> inTV(tValue: T, vValue: V) {
    }
}

class FunSignaturesSneaky {
    fun withSpaces(param: String) {
    }

    fun withLineFeed(param
                     : String) {
    }

    fun withDefaultValue(foo: String = "hello") {
    }

    fun withModifiers(foo: String, bar: Boolean) {
    }

    fun withModifiers(vararg foo: String, bar: Boolean) {
    }

    fun withModifiers(foo: String, vararg bar: Boolean) {
    }

    fun withArgAnnotationsAndTabs(	@SomeAnnotation(param1 = arrayOf("",     "path")) path: String?,
						   @SomeAnnotation(param1 = ["invert"], param2 = "false",
							   param3 = "true") invert: Boolean): String {
        return ""
    }

    infix fun Int.infixFun(x: Int): Int {
        return this + x
    }

    fun Int.extensionInClass(foo: String): Int {
        return this
    }

    companion object {
        fun Int.extensionInCompanion(foo: String) {
        }
    }

    annotation class SomeAnnotation(val param1: Array<String>, val param2: String = "", val param3: String = "")
}

fun topLevel(foo: Int) {
}

fun Int.extensionAtTopLevel(foo: String) {
}
