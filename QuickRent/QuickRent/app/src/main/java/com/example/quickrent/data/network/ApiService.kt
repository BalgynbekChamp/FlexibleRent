package com.example.quickrent.network

import com.example.quickrent.network.model.LoginRequest
import com.example.quickrent.network.model.LoginResponse
import com.example.quickrent.data.model.RegisterRequest
import com.example.quickrent.data.model.RegisterResponse
import com.example.quickrent.data.model.ListingDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("/api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/api/users/logout")
    fun logout(@Header("Authorization") token: String): Call<Void>

    @POST("api/listings")
    suspend fun createListing(
        @Header("Authorization") token: String,
        @Body listing: ListingDTO
    ): Response<Void>
    @Multipart
    @POST("api/listings/with-photo")
    suspend fun uploadListingWithPhotos(
        @Header("Authorization") token: String,
        @Part photos: List<MultipartBody.Part>
        // Можно также передавать JSON часть в виде @Part("data") RequestBody
    ): Response<Void>


}
