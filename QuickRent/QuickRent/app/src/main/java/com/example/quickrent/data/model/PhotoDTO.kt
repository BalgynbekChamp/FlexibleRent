package com.example.quickrent.data.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoDTO(
    val id: Long? = null,
    val filePath: String,
    val fileName: String,
    val size: Long,
    val entityType: String = "LISTING",
    val entityId: Long? = null,
    val uploadedAt: String? = null
):Parcelable
