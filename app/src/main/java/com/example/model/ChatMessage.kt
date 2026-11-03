package com.example.model

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val suggestedFollowups: List<String> = emptyList()
)
