package org.jetbrains.ngramgenerator.generating

import org.jetbrains.ngramgenerator.structures.Tree

typealias Grams = MutableMap<String, Int>
typealias Nodes = MutableList<Tree>

class NgramGeneratorByTree(private val d: Int): NgramGenerator(d) {
    private fun buildNgrams(node: Tree, path: Nodes) {
        val visitedNodes = linkedSetOf<Tree>()
        val usedNodes = linkedSetOf<Tree>()
        var visitedAreaStarted = false
        path.asReversed().forEach {
            val isVisited = visitedNodes.contains(it)
            if (!isVisited) {
                if (visitedAreaStarted) {
                    visitedAreaStarted = false
                    usedNodes.forEach { visitedNodes.remove(it) }
                    visitedNodes.add(usedNodes.last())
                    usedNodes.clear()
                }
                visitedNodes.add(it)
                if (visitedNodes.size <= n * d + n - 1) {
                    buildNgramsByPath(visitedNodes, node)
                }
            } else if (!visitedAreaStarted) {
                visitedAreaStarted = true
            }

            if (isVisited) {
                usedNodes.add(it)
            }
        }
    }

    private fun dfw(node: Tree, path: Nodes) {
        ngramAdd(listOf(node.type)) // add unigram
        buildNgrams(node, path) // build bigram and 3-gram

        path.add(node)
        if (node.children != null) {
            node.children.map {
                dfw(it, path)
                path.add(node)
            }
            path.add(node)
        }
    }

    fun generate(tree: Tree): Grams {
        ngrams.clear()
        dfw(tree, mutableListOf())

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