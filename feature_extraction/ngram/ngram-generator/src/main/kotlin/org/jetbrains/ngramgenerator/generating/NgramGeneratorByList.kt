package org.jetbrains.ngramgenerator.generating

import org.jetbrains.ngramgenerator.structures.SimpleNode

class NgramGeneratorByList(private val d: Int): NgramGenerator(d) {
    companion object {
        fun linearizeMapOfList(map: Map<String, List<String>>): List<String> {
            val list: MutableList<String> = mutableListOf()
            map.map { list.addAll(it.value) }
            return list
        }
    }

    private fun listWalk(list: List<String>) {
        val history = linkedSetOf<SimpleNode>()

        list.forEach {
            if (history.size > d * n + n - 1) {
                history.remove(history.first())
            }
            val currentNode = SimpleNode(it)
            if (history.size != 0) {
                ngramAdd(listOf(history.last().type, "0", it))
            }
            if (history.size == n - 1) {
                ngramAdd(listOf(history.first().type, "0", history.last().type, "0", it))
            }
            ngramAdd(listOf(it))
            history.add(currentNode)
        }
    }

    fun generate(list: List<String>): Grams {
        ngrams.clear()
        listWalk(list)
        ngrams.map {
            if (allNgrams.contains(it.key)) {
                allNgrams[it.key] = allNgrams[it.key] !!+ it.value
            } else {
                allNgrams[it.key] = it.value
            }
        }

        return ngrams
    }
}