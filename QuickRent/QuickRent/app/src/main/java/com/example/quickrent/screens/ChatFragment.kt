package com.example.quickrent.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.quickrent.R
import com.example.quickrent.network.RetrofitClient
import kotlinx.coroutines.launch
import com.example.quickrent.data.model.ChatDTO

class ChatFragment : Fragment() {

    private lateinit var sendButton: Button
    private lateinit var messageEditText: EditText
    private lateinit var messagesListView: ListView
    private val messages = mutableListOf<String>()  // Здесь будут храниться сообщения

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        sendButton = view.findViewById(R.id.sendButton)
        messageEditText = view.findViewById(R.id.messageEditText)
        messagesListView = view.findViewById(R.id.messagesListView)

        sendButton.setOnClickListener {
            sendMessage()
        }

        // Пример простого списка сообщений
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, messages)
        messagesListView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.chatToolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val chatId = arguments?.getLong("chatId") ?: -1L
        if (chatId != -1L) {
            loadChatAndUser(chatId)
        } else {
            Toast.makeText(requireContext(), "Ошибка: chatId не найден", Toast.LENGTH_SHORT).show()
        }
    }


    private fun sendMessage() {
        val message = messageEditText.text.toString()
        if (message.isNotEmpty()) {
            messages.add(message)
            messageEditText.text.clear()

            // Обновляем список сообщений
            (messagesListView.adapter as ArrayAdapter<String>).notifyDataSetChanged()
        }
    }

    private fun loadChatAndUser(chatId: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")
        val currentUserId = sharedPreferences.getLong("user_id", -1)

        lifecycleScope.launch {
            try {
                val chatResponse = RetrofitClient.api.getChatById(token, chatId)
                if (chatResponse.isSuccessful) {
                    val chat = chatResponse.body()
                    chat?.let {
                        // Используем participantOneId и participantTwoId для определения другого пользователя
                        val otherUserId = if (it.participantOneId == currentUserId) it.participantTwoId else it.participantOneId

                        // Получаем информацию о другом пользователе
                        val userResponse = RetrofitClient.api.getUserById(token, otherUserId)
                        if (userResponse.isSuccessful) {
                            val user = userResponse.body()
                            val toolbar: Toolbar = requireView().findViewById(R.id.chatToolbar)
                            toolbar.title = " ${user?.firstName} ${user?.lastName}"
                        } else {
                            Toast.makeText(requireContext(), "Ошибка загрузки пользователя", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("ChatFragment", "Ошибка: ${chatResponse.code()} - ${chatResponse.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Ошибка загрузки чата", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }





    companion object {
        // Теперь метод принимает chatId
        fun newInstance(chatId: Long): ChatFragment {
            val fragment = ChatFragment()
            val args = Bundle()
            args.putLong("chatId", chatId) // Передаем chatId в аргументы
            fragment.arguments = args
            return fragment
        }
    }
}
