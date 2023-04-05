package com.example.demoandroidplugin

import com.example.demoandroidplugin.ui.JsonInputDialog
import com.example.demoandroidplugin.utils.executeCouldRollBackAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.net.URL
import kotlin.math.max

class MyPluginAction : AnAction() {
    override fun update(e: AnActionEvent) {
        super.update(e)
    }

    override fun actionPerformed(event: AnActionEvent) {
        var jsonString = ""
        try {
            val project = event.getData(PlatformDataKeys.PROJECT)
            val caret = event.getData(PlatformDataKeys.CARET)
            val editor = event.getData(PlatformDataKeys.EDITOR_EVEN_IF_INACTIVE)
            val document = editor?.document ?: return
            if (couldNotInsertCode(editor)) return
            var tempClassName = ""
            val inputDialog = JsonInputDialog(tempClassName, project!!)
            inputDialog.show()
            val className = inputDialog.getClassName()
            val inputString = inputDialog.inputString
            val json = if (inputString.startsWith("http")) {
                URL(inputString).readText()
            } else inputString
            if (json.isEmpty()) {
                return
            }
            jsonString = json
            val offset = calculateOffset(caret, document)
            insertKotlinCode(project!!, document, offset, jsonString)
        } catch (e: Throwable) {
            Messages.showMessageDialog(
                event.project,
                "Sorry, something went wrong...Blame Jacky",
                "Oops",
                Messages.getInformationIcon()
            );
            throw e
        }
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

    private fun couldNotInsertCode(editor: Editor?): Boolean {
        if (editor == null || editor.document.isWritable.not()) {
            Messages.showWarningDialog(
                "Please open a file in edited state for inserting Kotlin code!",
                "No Edited File"
            )
            return true
        }
        return false
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