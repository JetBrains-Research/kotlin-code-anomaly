@file:Suppress("EXPERIMENTAL_FEATURE_WARNING", "unused", "UNUSED_VARIABLE", "RedundantSuspendModifier",
        "UNUSED_ANONYMOUS_PARAMETER", "SimplifyBooleanWithConstants")

package testSrc

import org.jetbrains.annotations.NotNull

fun f1() {
    for (i in 1..10) {
        println("Hello!")
    }
    while (2 > 3) {
    }
}

annotation class SomeAnnotation

@NotNull
@SomeAnnotation
suspend fun f2(arg: Int?) {
    arg!!.toString()
    val a = { x: Any -> { y: Any -> y.toString() } }
    val b = { y: Any -> y.toString() }
}
