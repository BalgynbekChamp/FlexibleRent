package com.example.quickrent.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.quickrent.R
import com.example.quickrent.data.model.ListingDTO
import com.example.quickrent.network.RetrofitClient
import kotlinx.coroutines.launch


class ListingDetailsFragment : Fragment() {

    private lateinit var listing: ListingDTO
    private lateinit var rentButton: Button
    private lateinit var messageButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listing_details, container, false)
        // Получаем данные из аргументов
        listing = arguments?.getParcelable("listing") ?: return view

        // Заполняем UI данными
        val titleTextView: TextView = view.findViewById(R.id.listingTitleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.listingDescriptionTextView)
        val priceTextView: TextView = view.findViewById(R.id.listingPriceTextView)
        val imageView: ImageView = view.findViewById(R.id.listingImageView)

        titleTextView.text = listing.title
        descriptionTextView.text = listing.description
        priceTextView.text = "${listing.price} ₸"

        Glide.with(view.context)
            .load(listing.photos.firstOrNull()?.filePath ?: "")
            .into(imageView)

        // Инициализация кнопок
        rentButton = view.findViewById(R.id.rentButton)
        messageButton = view.findViewById(R.id.messageButton)

        // Обработчик кнопки "Арендовать"
        rentButton.setOnClickListener {
            onRentButtonClicked()
        }

        // Обработчик кнопки "Написать"
        messageButton.setOnClickListener {
            onMessageButtonClicked()
        }

        return view
    }

    private fun onRentButtonClicked() {
        // Логика аренды
        Toast.makeText(requireContext(), "Арендовать!", Toast.LENGTH_SHORT).show()
        // Здесь вы можете добавить логику перехода на экран аренды
    }

    private fun onMessageButtonClicked() {
        // Получаем ID текущего пользователя и ID арендодателя
        val currentUserId = getCurrentUserId() // Метод для получения текущего пользователя
        val landlordUserId = listing.userId // ID арендодателя из объекта listing

        Log.d("Auth", "Текущий пользователь ID: $currentUserId, Арендодатель ID: $landlordUserId")

        // Проверяем, существует ли чат между текущим пользователем и арендодателем
        checkExistingChat(currentUserId, landlordUserId)
    }

    private fun checkExistingChat(currentUserId: Long, landlordUserId: Long) {
        val token = getAuthToken()
        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Токен не найден", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            runCatching {
                RetrofitClient.api.getChatsByParticipants(token, currentUserId, landlordUserId) // Метод для получения чатов по участникам
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val chats = response.body()
                    if (chats.isNullOrEmpty()) {
                        // Чат не найден, создаём новый
                        createChat(currentUserId, landlordUserId)
                    } else {
                        // Чат существует, переходим к существующему чату
                        val existingChat = chats.firstOrNull()
                        existingChat?.let {
                            val chatFragment = ChatFragment.newInstance(it.id)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, chatFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Ошибка проверки чатов: ${response.code()} ${response.message()}")
                    Toast.makeText(requireContext(), "Ошибка проверки чатов", Toast.LENGTH_SHORT).show()
                }
            }.onFailure { exception ->
                Log.e("API_ERROR", "Ошибка запроса: ${exception.localizedMessage}")
                Toast.makeText(requireContext(), "Ошибка: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createChat(currentUserId: Long, landlordUserId: Long) {
        if (currentUserId == -1L) {
            Toast.makeText(requireContext(), "Пользователь не аутентифицирован", Toast.LENGTH_SHORT).show()
            return
        }

        val token = getAuthToken()
        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Токен не найден", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Auth", "Токен при создании чата: $token")
        Log.d("Chat", "currentUserId: $currentUserId, landlordUserId: $landlordUserId")

        lifecycleScope.launch {
            runCatching {
                RetrofitClient.api.createChat(token, currentUserId, landlordUserId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val chatId = response.body()?.id
                    if (chatId != null) {
                        Toast.makeText(requireContext(), "Чат успешно создан", Toast.LENGTH_SHORT).show()

                        val chatFragment = ChatFragment.newInstance(chatId)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, chatFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(requireContext(), "Не удалось получить ID чата", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка создания чата: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Ошибка: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getAuthToken(): String {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        return "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")
    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1)
    }

    companion object {
        fun newInstance(listing: ListingDTO): ListingDetailsFragment {
            val fragment = ListingDetailsFragment()
            val args = Bundle()
            args.putParcelable("listing", listing)
            fragment.arguments = args
            return fragment
        }
    }
}
