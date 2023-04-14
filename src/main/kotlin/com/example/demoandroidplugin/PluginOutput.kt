package com.example.demoandroidplugin

import com.example.demoandroidplugin.utils.executeCouldRollBackAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import kotlin.math.max

interface PluginOutput {
    fun insertCode(code: String)
}

class PluginOutputImpl(
    private val project: Project,
    private val document: Document,
    private val caret: Caret?
) : PluginOutput {
    override fun insertCode(content: String) {
        val offset = calculateOffset(caret, document)
        insertKotlinCode(project, document, offset, content)
    }

    private fun insertKotlinCode(
        project: Project?,
        document: Document,
        offset: Int,
        content: String
    ): Boolean {
        executeCouldRollBackAction(project) {
            document.insertString(
                max(offset, 0),
                content
            )
        }

        return true
    }

    private fun calculateOffset(caret: Caret?, document: Document): Int {
        var offset: Int
        if (caret != null) {

            offset = caret.offset
            if (offset == 0) {
                offset = document.textLength
            }
            val lastPackageKeywordLineEndIndex = try {
                "^[\\s]*package\\s.+\n$".toRegex(RegexOption.MULTILINE).findAll(document.text).last().range.last
            } catch (e: Exception) {
                -1
            }
            val lastImportKeywordLineEndIndex = try {
                "^[\\s]*import\\s.+\n$".toRegex(RegexOption.MULTILINE).findAll(document.text).last().range.last
            } catch (e: Exception) {
                -1
            }
            if (offset < lastPackageKeywordLineEndIndex) {
                offset = lastPackageKeywordLineEndIndex + 1
            }
            if (offset < lastImportKeywordLineEndIndex) {
                offset = lastImportKeywordLineEndIndex + 1
            }

        } else {
            offset = document.textLength
        }

        return offset
    }
}