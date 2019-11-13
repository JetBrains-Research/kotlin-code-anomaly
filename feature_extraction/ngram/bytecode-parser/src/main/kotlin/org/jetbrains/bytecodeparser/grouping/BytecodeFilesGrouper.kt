package org.jetbrains.bytecodeparser.grouping

import org.jetbrains.bytecodeparser.io.FileWriter
import java.io.File
import java.nio.file.Files

typealias Classes = MutableSet<String>
typealias ClassesByPackage = MutableMap<String, Classes>
typealias ClassesByRepo = MutableMap<String, ClassesByPackage>

class BytecodeFilesGrouper(private val packagesOutputDirectory: String) {
    companion object {
        const val BYTECODE_JSON_EXT = "class.json"
        const val CLASS_USAGES_MAP_DIRECTORY = "usages"
        const val CLASS_PACKAGES_DIRECTORY = "packages"
    }

    private val bytecodeFilenameRegex = Regex("(?:(?<package>.+?)\\.)?(?<class>[^.]+).$BYTECODE_JSON_EXT")
    private val classesByRepo: ClassesByRepo = mutableMapOf()

    fun group(file: File, username: String, repo: String) {
        val match = bytecodeFilenameRegex.matchEntire(file.name)

        if (match == null) {
            println("MATCHING ERROR: $file")
            return
        }

        val packagesDirectory = "$packagesOutputDirectory/$CLASS_PACKAGES_DIRECTORY"
        val packageName = if (match.groups["package"] != null) match.groups["package"]!!.value else ""
        val className = match.groups["class"]!!.value
        val repoIdentifier = "$username:$repo"

        if (!classesByRepo.contains(repoIdentifier)) {
            classesByRepo[repoIdentifier] = mutableMapOf()
        }

        val classesByPackageInRepo = classesByRepo[repoIdentifier]

        if (!classesByPackageInRepo!!.contains(packageName)) {
            classesByPackageInRepo[packageName] = mutableSetOf()
        }

        classesByPackageInRepo[packageName]!!.add(className)

        val basePath: String
        if (packageName.isNotEmpty()) {
            val packagePath = packageName.split(".").joinToString("/")
            val packageFullPath = "$packagesDirectory/$packagePath"

            File(packageFullPath).mkdirs()
            basePath = "$packageFullPath/$className"
        } else {
            basePath = "$packagesDirectory/$className:$username:$repo"
        }
        val destFilepath = File("$basePath.$BYTECODE_JSON_EXT")
        if (!Files.exists(destFilepath.toPath())) {
            file.copyTo(destFilepath)
        }
    }

    fun writeClassUsagesMap() {
        val usagesDirectory = "$packagesOutputDirectory/$CLASS_USAGES_MAP_DIRECTORY"

        File(usagesDirectory).mkdirs()
        classesByRepo.map {
            val repoName = it.key
            val repoClassUsages = it.value

            FileWriter.write("$usagesDirectory/$repoName.json", repoClassUsages)
        }
    }
}