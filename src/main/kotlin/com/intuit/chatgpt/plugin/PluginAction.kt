package com.intuit.chatgpt.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.runBlocking

class PluginAction : AnAction() {
    private val dependencyInjector by lazy { PluginDependencyInjectorImpl() }

    override fun actionPerformed(event: AnActionEvent) {
        try {
            val caret = event.getData(PlatformDataKeys.CARET)
            val editor = event.getData(PlatformDataKeys.EDITOR_EVEN_IF_INACTIVE)
            if (couldNotInsertCode(editor)) return
            val document = editor?.document ?: return

            val inputDialog = dependencyInjector.provideInputDialog(event.project!!)
            inputDialog.show()
            val inputString = inputDialog.inputString ?: return
            if (inputString.isBlank()) return
            var outputString = ""
            val repository = dependencyInjector.getRepository(dependencyInjector.pluginState(event.project!!))
            runBlocking {
                outputString = repository.query(inputString).toString()
            }

            val pluginOutput = dependencyInjector.providePluginOutput(
                project = event.project!!,
                document = document,
                caret = caret
            )
            pluginOutput.insertCode(outputString)
        } catch (e: Throwable) {
            Messages.showMessageDialog(
                event.project,
                "Sorry, something went wrong...${e.message} \n ${e.stackTraceToString()}",
                "Oops",
                Messages.getInformationIcon()
            )

            throw e
        }
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
}