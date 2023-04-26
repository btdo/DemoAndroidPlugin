package com.intuit.chatgpt.plugin.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.util.ui.JBUI
import com.intuit.chatgpt.plugin.PluginPersistenceService
import com.intuit.chatgpt.plugin.model.PluginState
import com.intuit.chatgpt.plugin.ui.*
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

class PluginSettings(private val project: Project) : Configurable, DocumentListener {
    private var modified = false
    private val state: PluginState by lazy {
        PluginPersistenceService.getInstance(project).state
    }

    private val urlField = createTextFieldComponent()
    private val apiKeyField = createTextFieldComponent()
    private val modelField = createTextFieldComponent()

    private fun createTextFieldComponent(): JTextComponent {
        return jTextInput {
            document.addDocumentListener(this@PluginSettings)
        }
    }

    private val panel: JPanel = jBorderLayout {
        jVerticalLinearLayout {
            jHorizontalLinearLayout {
                jLabel("url:        ", 14f)
                add(urlField)
            }
            jHorizontalLinearLayout {
                jLabel("apiKey: ", 14f)
                add(apiKeyField)
            }
            jHorizontalLinearLayout {
                jLabel("model:  ", 14f)
                add(modelField)
            }
        }
    }

    override fun createComponent(): JComponent? {
        urlField.text = state.url
        apiKeyField.text = state.apiKey
        modelField.text = state.model
        return panel
    }

    override fun isModified(): Boolean  = modified

    override fun apply() {
        state.apiKey = apiKeyField.text
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