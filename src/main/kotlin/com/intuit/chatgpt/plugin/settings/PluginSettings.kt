package com.intuit.chatgpt.plugin.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.panel
import com.intuit.chatgpt.plugin.PluginPersistenceService
import com.intuit.chatgpt.plugin.model.PluginState
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class PluginSettings(private val project: Project) : Configurable, DocumentListener {
    private var modified = false
    private val state: PluginState by lazy {
        PluginPersistenceService.getInstance(project).state
    }

    private val urlField: JTextField = JTextField()
    private val apiKeyField: JPasswordField = JPasswordField()
    private val modelField: JTextField = JTextField()

    private val panel: JPanel = panel {
        row("ChatGPT Url") { urlField }
        row("API Key") { apiKeyField }
        row("model") { modelField }
    }

    override fun createComponent(): JComponent? {
        apiKeyField.apply {
            text = state.apiKey
            document.addDocumentListener(this@PluginSettings)
        }

        urlField.apply {
            text = state.url
            document.addDocumentListener(this@PluginSettings)
        }

        modelField.apply {
            text = state.model
            document.addDocumentListener(this@PluginSettings)
        }

        return panel
    }

    override fun isModified(): Boolean  = modified

    override fun apply() {
        state.apiKey = String(apiKeyField.password)
        state.url = urlField.text
        state.model = modelField.text
        PluginPersistenceService.getInstance(project).loadState(state)
        modified = false
    }

    override fun getDisplayName(): String = "Unit Test Generator Settings"

    override fun insertUpdate(e: DocumentEvent?) {
        modified = true
    }

    override fun removeUpdate(e: DocumentEvent?) {
        modified = true
    }

    override fun changedUpdate(e: DocumentEvent?) {
        modified =true
    }
}