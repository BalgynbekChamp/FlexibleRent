package com.example.quickrent.network.model

import com.example.quickrent.data.model.UserDTO
import com.google.gson.annotations.SerializedName



data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: UserDTO
)