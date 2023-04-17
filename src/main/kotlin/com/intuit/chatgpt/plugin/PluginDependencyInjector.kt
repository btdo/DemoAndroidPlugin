package com.intuit.chatgpt.plugin

import com.intuit.chatgpt.plugin.ui.PluginInputDialog
import com.intuit.chatgpt.plugin.ui.PluginOutput
import com.intuit.chatgpt.plugin.ui.PluginOutputImpl
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intuit.chatgpt.plugin.model.PluginState
import com.intuit.chatgpt.plugin.network.AuthHeaderInterceptor
import com.intuit.chatgpt.plugin.network.ChatGptApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface PluginDependencyInjector {
    fun getRepository(state: PluginState): PluginRepository
    fun provideInputDialog(project: Project): Messages.InputDialog
    fun providePluginOutput(
        project: Project,
        document: Document,
        caret: Caret?
    ): PluginOutput

    fun pluginState(project: Project): PluginState
}

class PluginDependencyInjectorImpl() : PluginDependencyInjector {

    override fun providePluginOutput(project: Project, document: Document, caret: Caret?): PluginOutput =
        PluginOutputImpl(project, document, caret)

    override fun provideInputDialog(project: Project): Messages.InputDialog = PluginInputDialog(project)

    override fun pluginState(project: Project): PluginState = PluginPersistenceService.getInstance(project).state

    private fun getApi(state: PluginState): ChatGptApi {
        val httpClient =
            OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    HttpLoggingInterceptor.Level.BODY
            }).addInterceptor(AuthHeaderInterceptor(state.apiKey))
                .build()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder().client(httpClient)
            .baseUrl(state.url)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
            .create(ChatGptApi::class.java)
    }

    override fun getRepository(state: PluginState): PluginRepository {
        val api = getApi(state)
        return PluginRepositoryImpl(api)
    }
}


