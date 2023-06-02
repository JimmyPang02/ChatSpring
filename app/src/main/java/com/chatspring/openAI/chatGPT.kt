package com.chatspring.openAI

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.chatspring.GlobalapiKey
import com.chatspring.R
import com.chatspring.appsetting.SettingFragment
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

@OptIn(BetaOpenAI::class)
suspend fun chatGPT(prompt: String, input: String): String {
    return try {
        withTimeout(20_000) {

            val apiKey = GlobalapiKey

            val openAIhost =
                OpenAIHost("https://service-i501wcby-1318284291.jp.apigw.tencentcs.com")

            val openAIconfig = OpenAIConfig(token = apiKey, host = openAIhost)

            val openAI = OpenAI(openAIconfig)


            println("开始执行")

            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.User,
                        content = prompt + input
                    )
                )
            )

            var result: String? = "你好"
            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
            result = completion.choices[0].message?.content
            println(result)
            return@withTimeout result.toString()
        }
    } catch (e: TimeoutCancellationException) {
        println("Response timeout. Returning error message.")
        return "错误:AI响应超时"
    }
}

@OptIn(BetaOpenAI::class)
fun chatGPT_flow(prompt: String, input: String): Flow<ChatCompletionChunk> = flow {
    val apiKey = GlobalapiKey
    val openAIhost = OpenAIHost("https://service-i501wcby-1318284291.jp.apigw.tencentcs.com")
    val openAIconfig = OpenAIConfig(token = apiKey, host = openAIhost)
    val openAI = OpenAI(openAIconfig)

    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = prompt + input
            )
        )
    )

    try {
        openAI.chatCompletions(chatCompletionRequest).collect { chunk ->
            emit(chunk)
        }
    } catch (e: Exception) {
        // Here you can handle the exception, e.g., log it or notify user about the issue
        println("Error occurred: ${e.message}")
        // or rethrow the exception if you want to handle it on a higher level
        // throw e
    }
}
