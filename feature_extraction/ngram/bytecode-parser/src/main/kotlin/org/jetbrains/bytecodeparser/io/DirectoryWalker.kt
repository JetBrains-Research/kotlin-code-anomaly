package org.jetbrains.bytecodeparser.io

import java.io.File

class DirectoryWalker(private val dirPath: String, private val maxDepth: Int? = null) {
    private fun walkDirectory(callback: (File) -> Unit) {
        val dir = File(dirPath)
        var walkTopDown = dir.walkTopDown()

        if (maxDepth != null) {
            walkTopDown = walkTopDown.maxDepth(maxDepth)
        }
        walkTopDown.forEach { callback(it) }
    }

    fun run(callback: (File) -> Unit) {
        walkDirectory { file: File -> callback(file) }
    }
}