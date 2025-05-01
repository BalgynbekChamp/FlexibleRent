package com.example.quickrent.data.model

data class ChatDisplayModel(
    val chatId: Long,
    val otherUserId: Long,
    val otherUserName: String?,
    val otherUserPhone: String?,
    val otherUserAvatarUrl: String?,
    val createdAt: String
)

