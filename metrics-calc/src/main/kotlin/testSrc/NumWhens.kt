package testSrc

fun someWhens(foo: Int, bar: Int) {
    when (foo) {
        1 -> println(1)
        2 -> println(2)
        else -> {
            when (bar) {
                1 -> println(10)
                2 -> println(10)
                3 -> println(10)
                4 -> {
                    when {
                        foo + bar > 10 -> println()
                        else -> {
                        }
                    }
                }
            }

        }
    }
    // the answer should be 4
}
