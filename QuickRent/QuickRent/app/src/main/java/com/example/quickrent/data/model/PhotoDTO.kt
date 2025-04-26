package com.example.quickrent.data.model

data class PhotoDTO(
    val id: Long? = null,
    val filePath: String,
    val fileName: String,
    val size: Long,
    val entityType: String = "LISTING",
    val entityId: Long? = null,
    val uploadedAt: String? = null
)
