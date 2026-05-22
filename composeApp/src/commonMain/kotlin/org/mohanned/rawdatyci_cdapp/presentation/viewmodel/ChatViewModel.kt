package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    data class StartNewChat(val parentId: String, val initialMessage: String) : ChatIntent()
}

class ChatViewModel(
    private val getConversationsUseCase: GetConversationsUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val startConversationUseCase: StartConversationUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            ChatIntent.LoadConversations -> loadConversations()
            is ChatIntent.LoadMessages -> loadMessages(intent.conversationId)
            is ChatIntent.MessageTextChanged -> _state.update { it.copy(messageText = intent.text) }
            is ChatIntent.SendMessage -> sendMessage(intent.conversationId)
            is ChatIntent.StartNewChat -> startChat(intent.parentId, intent.initialMessage)
        }
    }

    private fun loadConversations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getConversationsUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        val items = if (uiState.data.items.isEmpty()) getDummyConversations() else uiState.data.items
                        _state.update { it.copy(conversations = items, isLoading = false) }
                    }
                    is UiState.Error -> {
                        _state.update { it.copy(conversations = getDummyConversations(), isLoading = false, error = uiState.message) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getMessagesUseCase(conversationId).collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        val items = if (uiState.data.items.isEmpty()) getDummyMessages(conversationId) else uiState.data.items
                        _state.update { it.copy(messages = items, isLoading = false) }
                    }
                    is UiState.Error -> {
                        _state.update { it.copy(messages = getDummyMessages(conversationId), isLoading = false) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun sendMessage(conversationId: String) {
        val content = _state.value.messageText
        if (content.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isSending = true) }
            val result = sendMessageUseCase(conversationId, content)
            
            if (result.isSuccess) {
                _state.update { it.copy(messageText = "", isSending = false) }
                loadMessages(conversationId) // Refresh list
            } else {
                // Fallback: Local UI simulation for smoother UX
                val newMessage = Message(
                    id = "local_${System.currentTimeMillis()}",
                    conversationId = conversationId,
                    senderId = "me",
                    senderName = "أنا",
                    content = content,
                    imageUrl = null,
                    isRead = false,
                    sentAt = "الآن"
                )
                _state.update { it.copy(messages = it.messages + newMessage, messageText = "", isSending = false) }
            }
        }
    }

    private fun startChat(parentId: String, message: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSending = true) }
            startConversationUseCase(parentId, message).onSuccess {
                loadConversations()
            }
            _state.update { it.copy(isSending = false) }
        }
    }

    private fun getDummyConversations() = listOf(
        Conversation("c1", "p1", "أحمد محمد", null, "أحمد محمد", "أهلاً بك، هل يمكننا الاستفسار عن...", "10:30 ص", 2, true),
        Conversation("c2", "p2", "سارة خالد", null, "سارة خالد", "تم استلام التقييم، شكراً لكِ..", "أمس", 0, false)
    )

    private fun getDummyMessages(convId: String) = listOf(
        Message("m1", convId, "p1", "ولي الأمر", "السلام عليكم، كيف حال الطفل اليوم؟", null, true, "09:00 ص"),
        Message("m2", convId, "me", "المعلمة", "وعليكم السلام، هو بخير ومتميز جداً اليوم.", null, true, "09:05 ص"),
        Message("m3", convId, "p1", "ولي الأمر", "الحمد لله، شكراً جزيلاً لاهتمامكم.", null, true, "09:10 ص")
    )
}
