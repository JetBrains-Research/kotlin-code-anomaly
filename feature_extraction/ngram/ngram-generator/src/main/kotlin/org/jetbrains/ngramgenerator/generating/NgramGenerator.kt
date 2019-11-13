package org.jetbrains.ngramgenerator.generating

import org.jetbrains.ngramgenerator.structures.AbstractNode
import java.util.LinkedHashSet

abstract class NgramGenerator(private val d: Int) {
    protected val n = 3
    protected val ngrams: Grams = mutableMapOf()
    val allNgrams: Grams = mutableMapOf()

    protected fun ngramAdd(gram: List<String>) {
        val gramStr = gram.joinToString(":")

        if (ngrams.contains(gramStr)) {
            ngrams[gramStr] = ngrams[gramStr]!!.inc()
        } else {
            ngrams[gramStr] = 1
        }
    }

    protected fun <TNode: AbstractNode>buildNgramsByPath(path: LinkedHashSet<TNode>, firstNodeOnPath: TNode) {
        val lastNodeOnPath = path.last()
        val distanceBetweenFirstAndLast = path.size - 1
        if (distanceBetweenFirstAndLast <= d) {
            ngramAdd(listOf(firstNodeOnPath.type, distanceBetweenFirstAndLast.toString(), lastNodeOnPath.type)) // add bigram
        }
        path.withIndex().forEach {
            if (it.value != lastNodeOnPath) {
                val distanceToFirst = it.index
                val distanceToLast = path.size - 2 - it.index
                if (distanceToFirst <= d && distanceToLast <= d) {
                    ngramAdd(listOf(firstNodeOnPath.type, distanceToFirst.toString(), it.value.type, distanceToLast.toString(), lastNodeOnPath.type)) // add 3-gram
                }
            }
        }
    }
}