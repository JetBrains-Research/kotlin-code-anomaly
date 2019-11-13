package org.jetbrains.bytecodeparser

import org.jetbrains.bytecodeparser.grouping.BytecodeFilesGrouper
import org.jetbrains.bytecodeparser.helpers.TimeLogger
import org.jetbrains.bytecodeparser.io.DirectoryWalker
import org.jetbrains.bytecodeparser.parsing.BytecodeParser
import org.jetbrains.bytecodeparser.parsing.ClassFilesExtractor
import java.io.File

enum class Stage {
    PARSING, GROUPING
}

object Runner {
    fun reposWalk(sourceDirectory: String, callback: (username: String, repo: String, directory: File) -> Unit) {
        DirectoryWalker(sourceDirectory, maxDepth = 2).run {
            if (it.isDirectory) {
                val repoIdentifier = it.relativeTo(File(sourceDirectory)).invariantSeparatorsPath.split("/")
                if (repoIdentifier.size == 2) {
                    callback(repoIdentifier[0], repoIdentifier[1], it)
                }
            }
        }
    }

    fun walkAndParse(parser: BytecodeParser, extractor: ClassFilesExtractor, directory: File, username: String, repo: String, isPrint: Boolean = true) {
        DirectoryWalker(directory.absolutePath).run {
            if (it.isFile && it.extension == BytecodeParser.JAR_FILE_EXT) {
                val classFilePaths = extractor.extract(it, username, repo)

                if (classFilePaths != null) {
                    classFilePaths.forEach { parser.parse(it, isPrint) }
                    if (isPrint) {
                        println("PARSED (${classFilePaths.size} CLASSES): ${it.absolutePath}")
                    }
                }
            }
        }
    }

    fun walkAndParse(jarsDirectory: String, directory: File, username: String, repo: String, isPrint: Boolean = true) {
        val parser = BytecodeParser()
        val extractor = ClassFilesExtractor(jarsDirectory)
        val timeLogger = TimeLogger(task_name = "PARSING BYTECODE")

        walkAndParse(parser, extractor, directory, username, repo, isPrint)
        timeLogger.finish()
    }

    fun parse(jarsDirectory: String) {
        val parser = BytecodeParser()
        val extractor = ClassFilesExtractor(jarsDirectory)
        val timeLogger = TimeLogger(task_name = "PARSING BYTECODE")

        reposWalk(jarsDirectory) { username: String, repo: String, directory: File ->
            walkAndParse(parser, extractor, directory, username, repo)
        }

        timeLogger.finish()
    }

    fun group(sourceClassesDirectory: String, packagesOutputDirectory: String) {
        val grouper = BytecodeFilesGrouper(packagesOutputDirectory)

        reposWalk(sourceClassesDirectory) { username: String, repo: String, directory: File ->
            DirectoryWalker(directory.absolutePath).run {
                if (it.isFile && it.name.endsWith(BytecodeFilesGrouper.BYTECODE_JSON_EXT)) {
                    grouper.group(it, username, repo)
                }
            }
            println("REPO PROCESSED: $directory")
        }

        grouper.writeClassUsagesMap()
    }

    fun run(stage: Stage, directory: String) {
       when (stage) {
           Stage.PARSING -> parse(directory)
           Stage.GROUPING -> group(directory, "${File(directory).parent}/classes_grouped")
       }
    }
}