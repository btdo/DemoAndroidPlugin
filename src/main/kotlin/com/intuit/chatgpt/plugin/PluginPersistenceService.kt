package com.intuit.chatgpt.plugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intuit.chatgpt.plugin.model.PluginState

@Service
@State(name ="PluginConfiguration", storages = [Storage(value="pluginConfiguration.xml")])
class PluginPersistenceService: PersistentStateComponent<PluginState> {

    private var state: PluginState = PluginState()

    override fun getState(): PluginState = state

    override fun loadState(state: PluginState) {
        this.state = state
    }

    companion object {
        fun getInstance(project: Project) : PluginPersistenceService = project.service()
    }
}