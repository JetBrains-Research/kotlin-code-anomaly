package org.jetbrains.bytecodetosourcemapper

import com.xenomachina.argparser.ArgParser

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val reposDirectory by parser.storing("-d", "--repos_directory", help="path to folder with repositories")

    RunnerByRepos.run(reposDirectory)
}