package com.example.quickrent.data.model

import com.google.gson.annotations.SerializedName

class RegisterRequest(
    @field:SerializedName("name") private val name: String,
    @field:SerializedName("lastname") private val lastname: String,
    @field:SerializedName("phone_number") private val phoneNumber: String,
    @field:SerializedName("email") private val email: String,
    @field:SerializedName("password") private val password: String,
    @field:SerializedName("confirm_password") private val confirmPassword: String
)
