@file:Suppress("unused", "UNUSED_PARAMETER")

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

    companion object {
        val NAMED_COMPANION = object {
            fun namedCompanion(`in`: String): String {
                return `in`
            }

            fun anotherNamedCompanion(size: Int): Array<Int?> {
                return arrayOfNulls(size)
            }
        }

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
}

fun topLevel(foo: Int) {
}

fun Int.extensionAtTopLevel(foo: String) {
}
