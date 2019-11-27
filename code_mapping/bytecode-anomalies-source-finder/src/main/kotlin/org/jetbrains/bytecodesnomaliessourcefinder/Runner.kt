package org.jetbrains.bytecodesnomaliessourcefinder

import com.fasterxml.jackson.core.type.TypeReference
import org.jetbrains.bytecodesnomaliessourcefinder.io.FileWriter
import org.jetbrains.bytecodesnomaliessourcefinder.io.JsonFilesReader
import org.jetbrains.bytecodesnomaliessourcefinder.structures.AnomalyItem
import org.jetbrains.bytecodesnomaliessourcefinder.structures.BytecodeAnomalyItem
import org.jetbrains.bytecodesnomaliessourcefinder.structures.BytecodeToSourceItem
import org.jetbrains.bytecodesnomaliessourcefinder.structures.SourceCodeAnomalyItem
import java.io.File

typealias Anomalies = List<List<Any>>
typealias Distances = Map<String, Float>

object Runner {
    private const val SOURCE_BYTECODE_MAP_FILE = "bytecode_to_source_map.json"

    private fun findSourceFile(anomalyClassPath: String, repoFolder: String, distancesMap: Distances): SourceCodeAnomalyItem {
        val anomalyClassPathSplited = anomalyClassPath.split("/")
        val sourceBytecodeMapFile = File("$repoFolder/${anomalyClassPathSplited[0]}/${anomalyClassPathSplited[1]}/$SOURCE_BYTECODE_MAP_FILE")

        if (!sourceBytecodeMapFile.exists()) {
            println("MAP FILE NOT FOUND")
            return SourceCodeAnomalyItem(null, null, null)
        }

        val bytecodeToSourceItemReference = object: TypeReference<Map<String, BytecodeToSourceItem>>() {}
        val bytecodeToSourceMap = JsonFilesReader.readFile<Map<String, BytecodeToSourceItem>>(sourceBytecodeMapFile, bytecodeToSourceItemReference)
        val classAbsolutePath = File("$repoFolder/$anomalyClassPath").absolutePath

        if (bytecodeToSourceMap.contains(classAbsolutePath)) {
            val sourceFile = bytecodeToSourceMap[classAbsolutePath]!!.file
            val psiFile = "${sourceFile.replace("/sources/", "/cst/")}.json"
            val anomalyPsiValue = if (distancesMap.contains(psiFile)) distancesMap[psiFile] else null

            println("[$anomalyPsiValue] $sourceFile — $psiFile")
            return SourceCodeAnomalyItem(sourceFile, psiFile, anomalyPsiValue)
        }

        println("$anomalyClassPath NOT FOUND IN MAP FILE")
        return SourceCodeAnomalyItem("", "", null)
    }

    private fun findBytecodeFile(anomalyPath: String, repoFolder: String, distancesMap: Distances): BytecodeAnomalyItem {
        val anomalyClassPathSplited = anomalyPath.split("/")
        val username = anomalyClassPathSplited[0]
        val repoName = anomalyClassPathSplited[1]
        val sourceBytecodeMapFile = File("$repoFolder/$username/$repoName/$SOURCE_BYTECODE_MAP_FILE")

        if (!sourceBytecodeMapFile.exists()) {
            println("MAP FILE NOT FOUND")
            return BytecodeAnomalyItem(null, null, null)
        }

        val sourceToBytecodeItemReference = object: TypeReference<Map<String, BytecodeToSourceItem>>() {}
        val sourceToBytecodeMap = JsonFilesReader.readFile<Map<String, BytecodeToSourceItem>>(sourceBytecodeMapFile, sourceToBytecodeItemReference)
        val psiJsonAbsolutePath = File("$repoFolder/$anomalyPath").absolutePath

        var foundAnomaly = BytecodeAnomalyItem("", "", null)

        sourceToBytecodeMap.forEach {
            val psiFilePath = it.value.file + ".json"
//            val psiFilePath = it.value.file.replace("/sources/", "/cst/") + ".json"
            if (psiFilePath == psiJsonAbsolutePath) {
                val bytecodeFile = it.key
                val bytecodeJsonFilename = bytecodeFile
                        .replace("$repoFolder/$username/$repoName/classes/", "")
                        .replace("/", ".") + ".json"

                val bytecodeFileSplited = bytecodeFile.replace("$repoFolder/", "").split("/")
                val bytecodeFileSplitedWithoutFilename = bytecodeFileSplited.subList(0, bytecodeFileSplited.size - 1).joinToString("/")
                val bytecodeJsonFile = "$repoFolder/$bytecodeFileSplitedWithoutFilename/$bytecodeJsonFilename"
                val anomalyBytecodeValue = if (distancesMap.contains(bytecodeJsonFile)) distancesMap[bytecodeJsonFile] else null

                foundAnomaly = BytecodeAnomalyItem(bytecodeFile, bytecodeJsonFile, anomalyBytecodeValue)

                if (anomalyBytecodeValue == null) {
                    println("NOT FOUND IN MAP FILE: $anomalyPath")
                } else {
                    println("FOUND: $anomalyPath - $bytecodeFile")
                }

                return@forEach
            }
        }

        return foundAnomaly
    }

    private fun buildBytecodeToSourceMap(anomalies: Anomalies, repoFolder: String, distancesMap: Distances): List<AnomalyItem> {
        val anomaliesStatistic = mutableListOf<AnomalyItem>()

        anomalies.forEach {
            val anomalyPath = it[0].toString()
            val anomalyBytecodeValue = it[1].toString().toFloat()
            val anomalyJsonFilename = anomalyPath.split("/").last()
            val anomalyClassName = anomalyJsonFilename.replace(".class.json", "").split(".").last()
            val anomalyClassPath = anomalyPath.replace(anomalyJsonFilename, "$anomalyClassName.class")
            val (anomalySourceFile, anomalyPsiFile, anomalyPsiValue) = findSourceFile(anomalyClassPath, repoFolder, distancesMap)

            anomaliesStatistic.add(
                    AnomalyItem(anomalyPath, anomalyClassPath, anomalyBytecodeValue, anomalyPsiValue, anomalySourceFile, anomalyPsiFile)
            )
        }

        return anomaliesStatistic
    }

    private fun buildSourceToBytecodeMap(anomalies: Anomalies, repoFolder: String, distancesMap: Distances): List<AnomalyItem> {
        val anomaliesStatistic = mutableListOf<AnomalyItem>()

        anomalies.forEach {
            val anomalyPath = it[0].toString().replace("repos_factorized_sparsed/", "")
            val anomalySourcePath = File(anomalyPath).nameWithoutExtension
            val anomalySourceValue = it[1].toString().toFloat()

            val (bytecodeFile, bytecodeJsonFile, anomalyBytecodeValue) = findBytecodeFile(anomalyPath, repoFolder, distancesMap)

            anomaliesStatistic.add(
                    AnomalyItem(bytecodeJsonFile, bytecodeFile, anomalyBytecodeValue, anomalySourceValue, anomalySourcePath, anomalyPath)
            )
        }

        return anomaliesStatistic
    }

    private fun buildDistancesMap(repoFolder: String, filesList: List<String>, distances: List<List<Any>>): Map<String, Float> {
        val distancesMap = mutableMapOf<String, Float>()

        distances.forEach {
            val index = it[0].toString().toInt()
//            val filePath = filesList[index].replace("repos_factorized_sparsed/", "$repoFolder/")
            val filePath = "$repoFolder/${filesList[index]}"

            distancesMap[File(filePath).absolutePath] = it[1].toString().toFloat()
        }

        return distancesMap
    }

    fun run(anomaliesFile: String, distancesFile: String, filesMapFile: String, repoFolder: String, anomaliesWithSourcesFile: String) {
        val anomaliesReference = object: TypeReference<Anomalies>() {}
        val filesListReference = object: TypeReference<List<String>>() {}
        val distancesReference = object: TypeReference<List<List<Any>>>() {}

        val anomalies = JsonFilesReader.readFile<Anomalies>(File(anomaliesFile), anomaliesReference)
        val filesList = JsonFilesReader.readFile<List<String>>(File(filesMapFile), filesListReference)
        val distances = JsonFilesReader.readFile<List<List<Any>>>(File(distancesFile), distancesReference)

        val distancesMap = buildDistancesMap(repoFolder, filesList, distances)
        val anomaliesStatistic = buildSourceToBytecodeMap(anomalies, repoFolder, distancesMap)

        FileWriter.write(anomaliesWithSourcesFile, anomaliesStatistic)
    }
}