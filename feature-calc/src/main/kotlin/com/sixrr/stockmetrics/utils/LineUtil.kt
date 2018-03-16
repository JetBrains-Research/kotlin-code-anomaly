/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.sixrr.stockmetrics.utils

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiCompiledElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace

object LineUtil {
    fun countLines(element: PsiElement): Int {
        if (element is PsiCompiledElement) {
            return 0
        }
        val text = element.text
        return countLines(text)
    }

    fun countCommentOnlyLines(comment: PsiComment): Int {
        val text = comment.text
        var totalLines = countLines(text)
        var isOnSameLineBeforeCode = false
        if (!endsInLineBreak(comment)) {
            var nextSibling: PsiElement? = comment.nextSibling

            while (nextSibling != null) {
                if (nextSibling is PsiComment || nextSibling is PsiWhiteSpace) {
                    if (containsLineBreak(nextSibling)) {
                        break
                    }
                } else {
                    isOnSameLineBeforeCode = true
                }
                nextSibling = nextSibling.nextSibling
            }
        }
        var isOnSameLineAfterCode = false
        var prevSibling: PsiElement? = comment.prevSibling
        while (prevSibling != null) {
            if (prevSibling is PsiComment || prevSibling is PsiWhiteSpace) {
                if (containsLineBreak(prevSibling)) {
                    break
                }
            } else {
                isOnSameLineAfterCode = true
            }
            prevSibling = prevSibling.prevSibling
        }

        if (isOnSameLineAfterCode) {
            totalLines = Math.max(totalLines - 1, 0)
        }
        if (isOnSameLineBeforeCode) {
            totalLines = Math.max(totalLines - 1, 0)
        }

        return totalLines
    }

    private fun countLines(text: String): Int {
        var lines = 0
        var onEmptyLine = true
        val chars = text.toCharArray()
        for (aChar in chars) {
            when (aChar) {
                '\n', '\r' -> if (!onEmptyLine) {
                    lines++
                    onEmptyLine = true
                }
                ' ', '\t' -> {
                    // don't do anything
                }
                else -> onEmptyLine = false
            }
        }
        if (!onEmptyLine) {
            lines++
        }
        return lines
    }

    private fun endsInLineBreak(element: PsiElement?): Boolean {
        val text = element?.text ?: return false
        val endChar = text[text.length - 1]
        return endChar == '\n' || endChar == '\r'
    }

    private fun containsLineBreak(element: PsiElement?): Boolean {
        val text = element?.text ?: return false
        return text.contains("\n") || text.contains("\r")
    }
}
