@file:Suppress("unused")

package testSrc

class FunSignatures {

    fun normal(foo: String): Int {
        return foo.length
    }

    fun oneLiner() = 42

    companion object {
        val NAMED_COMPANION = object {
            fun companion(`in`: String): String {
                return `in`
            }

            fun anotherCompanion(size: Int): Array<Int?> {
                return arrayOfNulls(size)
            }
        }
    }
}
