package testSrc

// The line below doesn't compile, but I've met funcs without parentheses after name in Kotlin code
//fun hello = "Oh, hello there!"

@Suppress("unused") // this is a test source
abstract class RandomAbstractClass {
    abstract fun abstractFun()

    fun funWithAs(): Int {
        @Suppress("UNUSED_VARIABLE")
        val x = ArrayList<Int>()
        @Suppress("CAST_NEVER_SUCCEEDS") // intentional
        return 42.0 as Int
    }

    fun funWithIs(): Boolean {
        @Suppress("USELESS_IS_CHECK") // intentional
        return ("42" is CharSequence)
    }
}
