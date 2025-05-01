package com.example.quickrent.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickrent.R
import com.example.quickrent.data.adapter.ChatListAdapter
import com.example.quickrent.data.model.ChatDTO
import com.example.quickrent.data.model.ChatDisplayModel
import com.example.quickrent.data.model.UserDTO
import com.example.quickrent.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val apiService = RetrofitClient.api

    private lateinit var token: String
    private var currentUserId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        recyclerView = view.findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Получаем токен и userId с помощью наших методов
        token = getAuthToken()
        currentUserId = getCurrentUserId()

        Log.d("NotificationFragment", "Token: $token")
        Log.d("NotificationFragment", "Current User ID: $currentUserId")

        if (token.isNotEmpty() && currentUserId != -1L) {
            loadChats()
        } else {
            Toast.makeText(requireContext(), "Қолданушы мәліметтері табылмады", Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun loadChats() {
        lifecycleScope.launch {
            try {
                Log.d("NotificationFragment", "Loading chats for user ID: $currentUserId")
                Log.d("NotificationFragment", "Using token: $token")

                // Получаем чаты для пользователя
                val response = apiService.getChatsForUser("Bearer $token", currentUserId)

                if (response.isSuccessful) {
                    Log.d("NotificationFragment", "Chats loaded successfully")
                    val chats = response.body() ?: emptyList()

                    Log.d("NotificationFragment", "Found ${chats.size} chats")

                    // Устанавливаем адаптер для RecyclerView
                    val chatPairs = chats.mapNotNull { chat ->
                        val otherUserId = when {
                            chat.participantOneId == currentUserId -> chat.participantTwoId
                            chat.participantTwoId == currentUserId -> chat.participantOneId
                            else -> null
                        }

                        if (otherUserId != null && otherUserId != 0L) {
                            async {
                                val userResponse = apiService.getUserById("Bearer $token", otherUserId)
                                val photoResponse = apiService.getPhotoByEntity("Bearer $token", "USER", otherUserId)

                                val user = userResponse.body()
                                val photo = photoResponse.body()?.firstOrNull() // если возвращается список

                                if (user != null) {
                                    val displayModel = ChatDisplayModel(
                                        chatId = chat.id,
                                        otherUserId = user.id,
                                        otherUserName = user.firstName,
                                        otherUserPhone = user.phoneNumber,
                                        otherUserAvatarUrl = photo?.filePath, // или fileName, если так нужно
                                        createdAt = chat.createdAt
                                    )
                                    displayModel
                                } else null
                            }
                        } else null
                    }.awaitAll().filterNotNull()

                    recyclerView.adapter = ChatListAdapter(chatPairs) { chat ->
                        // TODO: чат ашу логикасы осында жазылады
                    }


                } else {
                    Log.e("NotificationFragment", "Error loading chats: ${response.code()}")
                    Toast.makeText(requireContext(), "Қате: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("NotificationFragment", "Error: ${e.message}", e)
                Toast.makeText(requireContext(), "Сервер қатесі: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }



    private fun getAuthToken(): String {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        return (sharedPreferences.getString("auth_token", "") ?: "")
    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1)
    }
}

