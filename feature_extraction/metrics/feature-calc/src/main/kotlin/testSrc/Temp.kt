@file:Suppress("EXPERIMENTAL_FEATURE_WARNING", "unused", "UNUSED_VARIABLE", "RedundantSuspendModifier",
        "UNUSED_ANONYMOUS_PARAMETER", "SimplifyBooleanWithConstants")

package testSrc

import org.jetbrains.annotations.NotNull

fun f1() {
    println("")
    println("")
    println("")
    println("0")
    if ("3" == "1") {
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

fun f3() {
    String::class.java
    val sb = StringBuilder()
    sb
            .append(1)
            ?.append(2)!!
            .toString()
            .toIntOrNull()
            ?.plus(42)
    sb!!.append(4)!!.append(5)!!.toString()!!
}
