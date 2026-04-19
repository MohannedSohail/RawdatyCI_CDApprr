package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.domain.usecase.chat.*

data class ChatState(
    val conversations: List<Conversation> = emptyList(),
    val messages: List<Message> = emptyList(),
    val messageText: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null
)

sealed class ChatIntent {
    object LoadConversations : ChatIntent()
    data class LoadMessages(val conversationId: String) : ChatIntent()
    data class MessageTextChanged(val text: String) : ChatIntent()
    data class SendMessage(val conversationId: String) : ChatIntent()
    data class StartNewConversation(val participantId: String) : ChatIntent()
}

sealed class ChatEffect {
    data class ShowMessage(val message: String) : ChatEffect()
    data class NavigateToChat(val conversationId: String) : ChatEffect()
}

class ChatViewModel(
    private val getConversationsUseCase: GetConversationsUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val startConversationUseCase: StartConversationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ChatEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            ChatIntent.LoadConversations -> loadConversations()
            is ChatIntent.LoadMessages -> loadMessages(intent.conversationId)
            is ChatIntent.MessageTextChanged -> _state.update { it.copy(messageText = intent.text) }
            is ChatIntent.SendMessage -> sendMessage(intent.conversationId)
            is ChatIntent.StartNewConversation -> startConversation(intent.participantId)
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            getConversationsUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { it.copy(conversations = uiState.data.items, isLoading = false) }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            getMessagesUseCase(conversationId).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { it.copy(messages = uiState.data.items, isLoading = false) }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    private fun sendMessage(conversationId: String) {
        val content = _state.value.messageText.trim()
        if (content.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isSending = true, messageText = "") }
            val result = sendMessageUseCase(conversationId, content)
            if (result.isSuccess) {
                _state.update { it.copy(messages = it.messages + result.getOrThrow(), isSending = false) }
            } else {
                _effect.send(ChatEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل الإرسال"))
                _state.update { it.copy(isSending = false) }
            }
        }
    }

    private fun startConversation(participantId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = startConversationUseCase(participantId)
            if (result.isSuccess) {
                _effect.send(ChatEffect.NavigateToChat(result.getOrThrow().id))
            } else {
                _effect.send(ChatEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل بدء المحادثة"))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
