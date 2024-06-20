package com.example.smobileeapp.data

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {

    private const val api_key = "AIzaSyA36ew4xXczondieXvz5O0tBLfzigSs_5w"

    suspend fun getResponse(prompt: String): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro", apiKey = api_key
        )

        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }

            return Chat(
                prompt = response.text ?: "error",
                isFromUser = false
            )

        } catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "error",
                isFromUser = false
            )
        }

    }
}