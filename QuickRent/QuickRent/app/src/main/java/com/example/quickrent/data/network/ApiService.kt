package com.example.quickrent.network

import com.example.quickrent.network.model.LoginRequest
import com.example.quickrent.network.model.LoginResponse
import com.example.quickrent.data.model.RegisterRequest
import com.example.quickrent.data.model.RegisterResponse
import com.example.quickrent.data.model.ListingDTO
import com.example.quickrent.data.model.CategoryDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("/api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/api/users/logout")
    fun logout(@Header("Authorization") token: String): Call<Void>

    @POST("/api/listings")
    suspend fun createListing(
        @Header("Authorization") token: String,
        @Body listing: ListingDTO
    ): Response<ListingDTO>

    // Получить главные категории (parentId == null)
    @GET("api/categories/main")
    fun getMainCategories(@Header("Authorization") token: String): Call<List<CategoryDTO>>

    // Получить подкатегории по родителю
    @GET("api/categories/subcategories/{parentId}")
    fun getSubcategories(
        @Header("Authorization") token: String,  // Добавляем заголовок для токена
        @Path("parentId") parentId: Long
    ): Call<List<CategoryDTO>>

    @GET("api/listings/category/{categoryId}")
    fun getListingsByCategory(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: Long
    ): Call<List<ListingDTO>>

    @GET("/api/categories/subcategories")
    suspend fun getSubcategories(
        @Header("Authorization") token: String
    ): Response<List<CategoryDTO>>

}
