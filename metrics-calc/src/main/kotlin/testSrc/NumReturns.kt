// suppress warnings for test source
@file:Suppress("unused", "UseExpressionBody", "UNUSED_VARIABLE")

package testSrc

fun zeroReturns() {
    println("Oh, hello there!")
}

fun oneReturn(): Int {
    return 42
}

fun oneReturnWithLambda() {
    val foo = { list: List<Int> ->
        list.forEach lambda@ {
            if (it == 42) {
                return@lambda
            }
        }
    }
    return
}

fun zeroReturnsWithForEach() {
    (1..10).forEach {
        if (it == 42) {
            return@forEach
        }
    }
}

fun zeroReturnsWithAnonymousFun() {
    (1..10).forEach(fun(value: Int) {
        if (value == 8) return  // local return to the caller of the anonymous fun, i.e. the forEach loop
        print(value)
    })
}
