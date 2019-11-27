package org.jetbrains.bytecodesnomaliessourcefinder

import com.xenomachina.argparser.ArgParser

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val anomaliesFile by parser.storing("-a", "--anomalies_file", help="path to anomalies file")
    val distancesFile by parser.storing("-d", "--distances_file", help="path to distances (other dataset) file")
    val filesMapFile by parser.storing("-f", "--files_map_file", help="path to files map file (for specified distances)")
    val repoFolder by parser.storing("-r", "--repositories", help="path to folder of repositories with 'bytecode to source map' files")
    val anomaliesWithSourcesFile by parser.storing("-o", "--anomalies_with_sources_file", help="path to file, in witch will be written anomalies with mapped source files")

    Runner.run(anomaliesFile, distancesFile, filesMapFile, repoFolder, anomaliesWithSourcesFile)
}