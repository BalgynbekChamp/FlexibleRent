package com.example.quickrent.data.model

data class ChatDTO(
    val id: Long,
    val participantOneId: Long,
    val participantTwoId: Long,
    val createdAt: String
)
