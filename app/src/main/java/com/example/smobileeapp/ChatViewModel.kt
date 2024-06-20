package com.example.smobileeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smobileeapp.data.Chat
import com.example.smobileeapp.data.ChatData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    private var loadingJob: Job? = null

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    addPrompt(event.prompt)
                    addLoadingMessage()
                    getResponse(event.prompt)
                }
            }
            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    private fun addPrompt(prompt: String) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt, true))
                },
                prompt = ""
            )
        }
    }

    private fun addLoadingMessage() {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            var dots = 1
            _chatState.update { // 초기 메시지 추가
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        if (isEmpty() || !first().prompt.startsWith("AI가 응답을 생성하는 중입니다")) {
                            add(0, Chat("AI가 응답을 생성하는 중입니다", false))
                        }
                    }
                )
            }
            delay(500) // 0.5초 후 애니메이션 시작
            while (isActive) {
                val message = "AI가 응답을 생성하는 중입니다" + ".".repeat(dots)
                _chatState.update {
                    it.copy(
                        chatList = it.chatList.toMutableList().apply {
                            set(0, Chat(message, false)) // 첫 번째 메시지 업데이트
                        }
                    )
                }
                dots = (dots % 3) + 1
                delay(500)
            }
        }
    }

    private fun getResponse(prompt: String) {
        viewModelScope.launch {
            val chat = ChatData.getResponse(prompt)
            removeLoadingMessage()
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, chat)
                    }
                )
            }
        }
    }

    private fun removeLoadingMessage() {
        loadingJob?.cancel() // 로딩 작업 취소
        _chatState.update {
            it.copy(
                chatList = it.chatList.filter { !it.prompt.startsWith("AI가 응답을 생성하는 중입니다") }.toMutableList()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        loadingJob?.cancel() // ViewModel이 제거될 때 로딩 작업 취소
    }
}