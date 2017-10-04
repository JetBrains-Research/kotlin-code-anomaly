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

    private fun search(dir: File) {
        if (!dir.isDirectory || !dir.canRead()) {
            return
        }

        for (file in dir.listFiles()) {
            if (file.isDirectory) {
                search(file)
            } else {
                if (file.extension == "kt") {
                    ktFilesList.add(file)
                }
            }
        }
    }
}
