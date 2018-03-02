package io.github.ksmirenko.kotlin.metricsCalc.utils

import java.io.File

object KotlinFileUtils {
    private val ignoredFiles = hashMapOf(
            "amc.kt" to "amobconf__awesome-mobile-conferences/.github/amc.kt",
            "amc.kt" to "aweconf__awesome-conferences-database/.github/amc.kt"
    )

    fun isAcceptableKtFile(file: File): Boolean {
        return !file.isDirectory
                && file.extension == "kt"
                && !shouldIgnore(file)

    }

    fun shouldIgnore(file: File): Boolean {
        val pathSuffix = ignoredFiles[file.name] ?: return false
        return file.absolutePath.endsWith(pathSuffix)
    }
}
