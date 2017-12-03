package io.github.ksmirenko.kotlin.metricsCalc

import java.io.File

class KotlinFileFinder(
        private val rootDirectory: String
) {
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
            if (file.extension == "kt") {
                ktFilesList.add(file)
            }
            return
        }

        // Traverse the directory
        for (childFile in file.listFiles()) {
            if (childFile.isDirectory) {
                search(childFile)
            } else {
                if (childFile.extension == "kt") {
                    ktFilesList.add(childFile)
                }
            }
        }
    }
}
