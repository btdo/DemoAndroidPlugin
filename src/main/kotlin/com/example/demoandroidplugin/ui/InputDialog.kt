package com.example.demoandroidplugin.ui

import com.intellij.json.JsonFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
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

class InputDialog(project: Project) :
    Messages.InputDialog(
        project,
        "Please add your code here",
        "Generate Test Code",
        null,
        "",
        null
    ) {
    private lateinit var jsonContentEditor: Editor

    init {
        setOKButtonText("Generate")
        myField.text = ""
    }

    override fun createNorthPanel(): JComponent? {
        return jHorizontalLinearLayout {
            jIcon("/icons/icon_json_input_dialog.png")
            fixedSpace(5)
            jVerticalLinearLayout {
                alignLeftComponent {
                    jLabel("Testing", 12f)
                }
                jHorizontalLinearLayout {
                    jLabel("JSON Text: ", 14f)
                    jLabel("Tips: you can use JSON string, http urls or local file just right click on text area", 12f)
                    fillSpace()
                }
            }
        }
    }

    override fun createCenterPanel(): JComponent? {
        jsonContentEditor = createJsonContentEditor()
        myField = createTextFieldComponent()
        return jBorderLayout {
            putCenterFill(jsonContentEditor.component)
            bottomContainer {
                jVerticalLinearLayout {
                    fixedSpace(7)
                    jHorizontalLinearLayout {
                        jLabel("Class Name: ", 14f)
                        add(myField)
                    }
                    fixedSpace(3)
                }
            }
        }
    }

    private fun createJsonContentEditor(): Editor {
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument("").apply {
            setReadOnly(false)
            addDocumentListener(object : com.intellij.openapi.editor.event.DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    revalidate()
                }
            })
        }

        val editor = editorFactory.createEditor(document, null, JsonFileType.INSTANCE, false)

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
                    jsonContentEditor.document.setText(transferable.getTransferData(DataFlavor.stringFlavor).toString())
                }
            }
        }
    }

    private fun createLoadFromLocalFileMenu() = JMenuItem("Load from local file").apply {
        addActionListener {
            FileChooser.chooseFile(FileChooserDescriptor(true, false, false, false, false, false), null, null) { file ->
                val content = String(file.contentsToByteArray())
                ApplicationManager.getApplication().runWriteAction {
                    jsonContentEditor.document.setText(content.replace("\r\n", "\n"))
                }
            }
        }
    }

    /**
     * get the user input class name
     */
    fun getClassName(): String {
        return if (exitCode == 0) {
            val name = myField.text.trim()
            name.let { if (it.first().isDigit() || it.contains('$')) "`$it`" else it }
        } else ""
    }

    override fun getInputString(): String = if (exitCode == 0) jsonContentEditor.document.text.trim() else ""

    override fun getPreferredFocusedComponent(): JComponent? {
        return jsonContentEditor.contentComponent
    }

    private fun revalidate() {
        okAction.isEnabled = myField.text.isNotEmpty()
    }
}