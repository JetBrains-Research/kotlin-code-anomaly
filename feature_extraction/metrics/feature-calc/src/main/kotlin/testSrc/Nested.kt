@file:Suppress("unused", "UNUSED_VARIABLE")

package testSrc

fun nestedFuns() {
    fun foo() {
        println()
    }

    val a = {
        // is not considered a nested function!
        println()
    }
    val funLit = fun String.() {
        println("funLit()")
    }
    arrayOf(1, 2, -1).filter(fun(item) = item > 0)
    // expected numNestedFuns = 3
}

fun nestedClasses() {
    class A {
        fun foo() {
            class B
        }
    }

    class C {
        inner class D
    }

    class E
    // expected numNestedClasses = 5
}
