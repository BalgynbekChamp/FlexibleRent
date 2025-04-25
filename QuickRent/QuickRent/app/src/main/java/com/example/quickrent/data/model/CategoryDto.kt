package com.example.quickrent.model

data class CategoryDto(
    val id: Long,
    val name: String,
    val iconUrl: String?,  // Может быть null
    val parentId: Long?    // null, если это главная категория
)
