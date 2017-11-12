package testSrc

import java.util.*
import kotlin.system.measureTimeMillis

// The line below doesn't compile, but I've met funcs without parentheses after name in Kotlin code
//fun hello = "Oh, hello there!"

@Suppress("unused") // this is a test source
abstract class RandomAbstractClass {
    abstract fun abstractFun()

    fun funWithAs(): Int {
        @Suppress("CAST_NEVER_SUCCEEDS") // intentional
        return 42.0 as Int
    }

    fun funWithIs(): Boolean {
        @Suppress("USELESS_IS_CHECK") // intentional
        return ("42" is CharSequence)
    }
}

fun main(args: Array<String>) {
    val random = Random()
    val size = 100000
    val listOfLists = List(size) {
        List(1 + random.nextInt(9)) { random.nextInt() }
    }

    measure("Naive") { naive(listOfLists) }
    measure("ListSum") { listSum(listOfLists) }
    measure("ListAppend") { listAppend(listOfLists) }
    measure("FlatMap") { flatMap(listOfLists) }
    measure("Flatten") { flatten(listOfLists) }
    measure("Reduce") { reduce(listOfLists) }
}

private fun measure(name: String, func: () -> Unit): Boolean {
    println("$name:\t${measureTimeMillis(func)} ms")
    return true
}

@Suppress("LoopToCallChain")
private fun naive(listOfLists: List<List<Int>>) {
    val flattenList = mutableListOf<Int>()
    for (sublist in listOfLists) {
        for (element in sublist) {
            flattenList.add(element)
        }
    }
}

private fun listSum(listOfLists: List<List<Int>>) {
    var flattenList = listOf<Int>()
    for (sublist in listOfLists) {
        flattenList += sublist
    }
}

private fun listAppend(listOfLists: List<List<Int>>) {
    val flattenList = mutableListOf<Int>()
    for (sublist in listOfLists) {
        flattenList.addAll(sublist)
    }
}

private fun flatMap(listOfLists: List<List<Int>>) {
    listOfLists.flatMap { it }
}

private fun flatten(listOfLists: List<List<Int>>) {
    listOfLists.flatten()
}

private fun reduce(listOfLists: List<List<Int>>) {
    listOfLists.reduce { list1, list2 -> list1 + list2 }
}

// Sample results (size = 10^5):
// Naive:	      43 ms
// ListSum:    55523 ms
// ListAppend:    12 ms
// FlatMap:       14 ms
// Flatten:	      15 ms
// Reduce:	   56128 ms
