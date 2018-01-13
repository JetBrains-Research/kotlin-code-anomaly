package io.github.ksmirenko.kotlin.metricsCalc

import java.io.File

class KotlinFileFinder(
        private val rootDirectory: String
) {
    private val ignoredFiles = hashMapOf(
            "amc.kt" to "amobconf__awesome-mobile-conferences/.github/amc.kt"
    )

    private val ktFilesList = ArrayList<File>()

    fun search(): List<File> {
        val root = File(rootDirectory)
        search(root)
        return ktFilesList
    }

    private fun search(file: File) {
        if (!file.canRead()) {
            return
        }

        if (!file.isDirectory) {
            // Single file
            if (file.extension == "kt" && !shouldIgnore(file)) {
                ktFilesList.add(file)
            }
            return
        }

        // Traverse the directory
        for (childFile in file.listFiles()) {
            search(childFile)
        }
    }

    private fun shouldIgnore(file: File): Boolean {
        val pathSuffix = ignoredFiles[file.name] ?: return false
        return file.absolutePath.endsWith(pathSuffix)
    }
}
