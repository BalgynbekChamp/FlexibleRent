package com.example.quickrent.network.model

import com.google.gson.annotations.SerializedName

class LoginRequest(
    @field:SerializedName("email") private val email: String, @field:SerializedName(
        "password"
    ) private val password: String
)