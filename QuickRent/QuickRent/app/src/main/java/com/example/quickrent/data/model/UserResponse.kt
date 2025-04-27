package com.example.quickrent.network.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("password")
    val password: String? // может быть null
)
