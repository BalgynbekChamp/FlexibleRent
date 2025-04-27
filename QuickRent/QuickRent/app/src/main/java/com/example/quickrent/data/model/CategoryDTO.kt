package com.example.quickrent.data.model

data class CategoryDTO(
    val id: Long,
    val name: String,
    val iconUrl: String?,  // Может быть null
    val parentId: Long?    // null, если это главная категория
)
