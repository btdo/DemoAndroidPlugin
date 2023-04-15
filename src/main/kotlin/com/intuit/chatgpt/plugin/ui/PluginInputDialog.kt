package com.intuit.chatgpt.plugin.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.JBDimension
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import javax.swing.JComponent
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.text.JTextComponent

class PluginInputDialog(private val project: Project) :
    Messages.InputDialog(
        project,
        "Insert your code here",
        "Unit Test Generator",
        null,
        "",
        null
    ) {
    private lateinit var inputEditor: Editor

    init {
        setOKButtonText("Generate")
        myField.text = ""
        okAction.isEnabled = false
    }

    override fun createNorthPanel(): JComponent {
        return jHorizontalLinearLayout {
            jIcon("/icons/icon_json_input_dialog.png")
            fixedSpace(10)
            jLabel("Insert your code here to generate", 14f)
        }
    }

    override fun createCenterPanel(): JComponent {
        inputEditor = createInputEditor()
        myField = createTextFieldComponent()
        return jBorderLayout {
            putCenterFill(inputEditor.component)
        }
    }

    private fun createInputEditor(): Editor {
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument("").apply {
            setReadOnly(false)
            addDocumentListener(object : com.intellij.openapi.editor.event.DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    revalidate()
                }
            })
        }

        val editor = editorFactory.createEditor(document, project, PlainTextFileType.INSTANCE, false)

        editor.component.apply {
            isEnabled = true
            preferredSize = Dimension(640, 480)
            autoscrolls = true
        }

        val contentComponent = editor.contentComponent
        contentComponent.isFocusable = true
        contentComponent.componentPopupMenu = JPopupMenu().apply {
            add(createPasteFromClipboardMenuItem())
            add(createLoadFromLocalFileMenu())
        }

        return editor
    }

    override fun createTextFieldComponent(): JTextComponent {
        return jTextInput(maxSize = JBDimension(10000, 35)) {
            document = NamingConventionDocument()
        }
    }

    private fun createPasteFromClipboardMenuItem() = JMenuItem("Paste from clipboard").apply {
        addActionListener {
            val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                runWriteAction {
                    inputEditor.document.setText(transferable.getTransferData(DataFlavor.stringFlavor).toString())
                }
            }
        }
    }

    private fun createLoadFromLocalFileMenu() = JMenuItem("Load from local file").apply {
        addActionListener {
            FileChooser.chooseFile(FileChooserDescriptor(true, false, false, false, false, false), null, null) { file ->
                val content = String(file.contentsToByteArray())
                ApplicationManager.getApplication().runWriteAction {
                    inputEditor.document.setText(content.replace("\r\n", "\n"))
                }
            }
        }
    }

    override fun getInputString(): String = if (exitCode == 0) inputEditor.document.text.trim() else ""

    override fun getPreferredFocusedComponent(): JComponent {
        return inputEditor.contentComponent
    }

    private fun revalidate() {
        okAction.isEnabled = inputEditor.document.text.isNotBlank()
    }
}