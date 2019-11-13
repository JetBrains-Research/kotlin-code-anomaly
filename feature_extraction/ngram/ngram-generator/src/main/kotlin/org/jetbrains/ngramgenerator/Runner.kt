package org.jetbrains.ngramgenerator

import com.fasterxml.jackson.core.type.TypeReference
import org.jetbrains.ngramgenerator.generating.Grams
import org.jetbrains.ngramgenerator.generating.NgramGenerator
import org.jetbrains.ngramgenerator.generating.NgramGeneratorByList
import org.jetbrains.ngramgenerator.generating.NgramGeneratorByTree
import org.jetbrains.ngramgenerator.helpers.TimeLogger
import org.jetbrains.ngramgenerator.io.DirectoryWalker
import org.jetbrains.ngramgenerator.io.FileWriter
import org.jetbrains.ngramgenerator.io.JsonFilesReader
import org.jetbrains.ngramgenerator.structures.Tree
import java.io.File
import java.nio.file.Files

enum class StructureType {
    TREE, LIST
}

object FilesCounter {
    var counter = 0
}

object Runner {
    private const val ALL_NGRAMS_FILE_PATH = "./all_ngrams.json"
    private const val TOTAL_FILES = 880593

    private fun writeGeneratedNgrams(ngramGenerator: NgramGenerator) {
        val writeTimeLogger = TimeLogger(task_name = "N-grams write")
        FileWriter.write(ALL_NGRAMS_FILE_PATH, ngramGenerator.allNgrams)
        writeTimeLogger.finish()
    }

    private fun checkAlreadyExist(file: File, dirPath: String, targetDirPath: String): Boolean {
        val relativePath = file.relativeTo(File(dirPath))
        val outputPath = File("$targetDirPath/$relativePath")
        val fileExist = Files.exists(outputPath.toPath())

        if (fileExist) {
            FilesCounter.counter++
            println("SKIP: PSI ALREADY FACTORIZED (${FilesCounter.counter} out of $TOTAL_FILES)")
        }

        return fileExist
    }

    private fun generateByTree(ngramGenerator: NgramGeneratorByTree, treesPath: String, treeVectorsPath: String) {
        val treeReference = object: TypeReference<ArrayList<Tree>>() {}
        val additionalFileCheck = { file: File -> checkAlreadyExist(file, treesPath, treeVectorsPath) }

        JsonFilesReader<ArrayList<Tree>>(treesPath, ".kt.json", treeReference).run(additionalFileCheck) { content: ArrayList<Tree>, file: File ->
            FilesCounter.counter++
            if (content.size == 0) {
                println("SKIP: EMPTY PSI FILE (${FilesCounter.counter} out of $TOTAL_FILES)")
                return@run
            }
            val grams: Grams = ngramGenerator.generate(content[0])
            FileWriter.write(file, treesPath, treeVectorsPath, grams)
            println("(${FilesCounter.counter} out of $TOTAL_FILES) $file: ${grams.size} n-grams extracted")
        }
    }

    private fun generateByList(ngramGenerator: NgramGeneratorByList, listsPath: String, listVectorsPath: String) {
        val listReference = object: TypeReference<Map<String, List<String>>>() {}

        DirectoryWalker(listsPath, maxDepth = 2).run {
            if (it.isDirectory) {
                val repoIdentifier = it.relativeTo(File(listsPath)).invariantSeparatorsPath.split("/")
                if (repoIdentifier.size == 2) {
                    var gramsByRepo = 0
                    JsonFilesReader<Map<String, List<String>>>(it.absolutePath, "class.json", listReference).run { content: Map<String, List<String>>, file: File ->
                        val list = NgramGeneratorByList.linearizeMapOfList(content)
                        val grams: Grams = ngramGenerator.generate(list)
                        FileWriter.write(file, listsPath, listVectorsPath, grams)
                        gramsByRepo += grams.size
                    }
                    println("$repoIdentifier: $gramsByRepo n-grams extracted")
                }
            }
        }
    }

    fun run(structureType: StructureType, structuresPath: String, structureVectorsPath: String) {
        var ngramGenerator: NgramGenerator? = null
        val timeLogger = TimeLogger(task_name = "N-gram extraction")

        try {
            when (structureType) {
                StructureType.TREE -> {
                    ngramGenerator = NgramGeneratorByTree(d = 0)
                    generateByTree(ngramGenerator, structuresPath, structureVectorsPath)

                }
                StructureType.LIST -> {
                    ngramGenerator = NgramGeneratorByList(d = 0)
                    generateByList(ngramGenerator, structuresPath, structureVectorsPath)
                }
            }
        } catch (e: Exception) {
            println("EXCEPTION: $e")
        } finally {
            writeGeneratedNgrams(ngramGenerator!!)
        }

        timeLogger.finish(fullFinish = true)
        println("${ngramGenerator.allNgrams.size} n-grams extracted")
    }
}