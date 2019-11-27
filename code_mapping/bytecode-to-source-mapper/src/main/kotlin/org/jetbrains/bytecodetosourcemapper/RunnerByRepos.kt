package org.jetbrains.bytecodetosourcemapper

import org.jetbrains.bytecodetosourcemapper.helpers.TimeLogger
import org.jetbrains.bytecodetosourcemapper.io.DirectoryWalker
import java.io.File

object RunnerByRepos {
    private fun reposWalk(sourceDirectory: String, callback: (username: String, repo: String, directory: File) -> Unit) {
        DirectoryWalker(sourceDirectory, maxDepth = 2).run {
            if (it.isDirectory) {
                val repoIdentifier = it.relativeTo(File(sourceDirectory)).invariantSeparatorsPath.split("/")
                if (repoIdentifier.size == 2) {
                    callback(repoIdentifier[0], repoIdentifier[1], it)
                }
            }
        }
    }

    fun run(reposDirectory: String) {
        val timeLoggerCommon = TimeLogger(task_name = "REPOS PROCESS")
        reposWalk(reposDirectory) { username: String, repo: String, directory: File ->
            val timeLogger = TimeLogger(task_name = "REPO \"$username/$repo\" PROCESS")
            RunnerByDirs.run("${directory.absolutePath}/classes", "${directory.absolutePath}/sources")
            timeLogger.finish()
        }
        timeLoggerCommon.finish(fullFinish = true)
    }
}