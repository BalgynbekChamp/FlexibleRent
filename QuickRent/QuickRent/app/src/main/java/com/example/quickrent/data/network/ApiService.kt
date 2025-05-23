package com.example.quickrent.network

import com.example.quickrent.data.model.ChatDTO
import com.example.quickrent.network.model.LoginRequest
import com.example.quickrent.network.model.LoginResponse
import com.example.quickrent.data.model.RegisterRequest
import com.example.quickrent.data.model.RegisterResponse
import com.example.quickrent.data.model.ListingDTO
import com.example.quickrent.data.model.UserDTO

import com.example.quickrent.data.model.CategoryDTO
import com.example.quickrent.data.model.PhotoDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @POST("/api/chats/create")
    suspend fun createChat(
        @Header("Authorization") token: String,
        @Query("user1Id") user1Id: Long,
        @Query("user2Id") user2Id: Long
    ): Response<ChatDTO>

    @GET("/api/chats/{chatId}")
    suspend fun getChatById(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Long): Response<ChatDTO>

    @GET("api/chats/check")
    suspend fun getChatsByParticipants(
        @Header("Authorization") token: String,
        @Query("user1Id") user1Id: Long,
        @Query("user2Id") user2Id: Long
    ): Response<List<ChatDTO>>


    @GET("/api/categories/subcategories")
    suspend fun getSubcategories(
        @Header("Authorization") token: String
    ): Response<List<CategoryDTO>>

    @GET("/api/users/{id}")
    suspend fun getUserById(@Header("Authorization") token: String, @Path("id") userId: Long): Response<UserDTO>

    @GET("/api/chats/user/{userId}")
    suspend fun getChatsForUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    ): Response<List<ChatDTO>>

    @GET("photos")
    suspend fun getPhotoByEntity(
        @Header("Authorization") token: String,
        @Query("entityType") entityType: String,
        @Query("entityId") entityId: Long
    ): Response<List<PhotoDTO>>

    @GET("/api/listings/popular")
    fun getPopularListings(
        @Header("Authorization") token: String
    ): Call<List<ListingDTO>>

}
