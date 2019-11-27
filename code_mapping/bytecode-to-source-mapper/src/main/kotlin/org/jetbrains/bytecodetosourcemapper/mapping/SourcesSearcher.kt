package org.jetbrains.bytecodetosourcemapper.mapping

import org.jetbrains.bytecodetosourcemapper.io.DirectoryWalker
import org.jetbrains.bytecodetosourcemapper.structures.BytecodeFileMetaInfo
import org.jetbrains.bytecodetosourcemapper.structures.BytecodeToSourceMapElement
import java.util.regex.Pattern
import java.util.regex.Pattern.DOTALL

class SourcesSearcher(private val sourcesDirectory: String) {
    companion object {
        private const val SOURCE_FILE_EXT = "kt"
        private const val multiLineCommentRegex = "/\\*.*\\*/"
        private const val singleLineCommentRegex = "//.*(?=\\n)"
        private const val stringsRegex = "/\".*\"/"
        private const val multilineStringsRegex = "/\"\"\".*\"\"\"/"
        private const val packageRegex = "(?:^|\\s|;)package (?<package>(?:\\.?\\w+)+)(?:\\s|;|$)"
    }

    fun search(metaInfo: BytecodeFileMetaInfo): BytecodeToSourceMapElement? {
        var foundSource: BytecodeToSourceMapElement? = null

        DirectoryWalker(sourcesDirectory).run {
            if (it.isFile && it.extension == SOURCE_FILE_EXT && it.name == metaInfo.sourceFileName) {
                val sourceCode = it.readText()
                        .replace(multiLineCommentRegex.toRegex(RegexOption.DOT_MATCHES_ALL), "")
                        .replace(singleLineCommentRegex.toRegex(), "")
                        .replace(multilineStringsRegex.toRegex(RegexOption.DOT_MATCHES_ALL), "")
                        .replace(stringsRegex.toRegex(), "")
                val patternClass = Pattern.compile(packageRegex, DOTALL)
                val matcherClass = patternClass.matcher(sourceCode)

                if (matcherClass.find() && metaInfo.packageName == matcherClass.group("package")) {
                    foundSource = BytecodeToSourceMapElement(it, metaInfo.lineNumberBounds)
                    return@run
                }
            }
        }

        return foundSource
    }
}