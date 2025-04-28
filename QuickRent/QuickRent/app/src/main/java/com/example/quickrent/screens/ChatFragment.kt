package com.example.quickrent.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickrent.R
import com.example.quickrent.data.adapter.ChatAdapter
import com.example.quickrent.network.RetrofitClient
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private lateinit var sendButton: ImageButton
    private lateinit var messageEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private var currentUserId: Long = -1L


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)


        sendButton = view.findViewById(R.id.sendButton) as ImageButton
        messageEditText = view.findViewById(R.id.messageEditText)
        recyclerView = view.findViewById(R.id.RecyclerView)

        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getLong("user_id", -1)

        chatAdapter = ChatAdapter(currentUserId)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        (recyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true

        sendButton.setOnClickListener {
            sendMessage()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.chatToolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        toolbar.setTitleTextColor(resources.getColor(android.R.color.white))  // Черный цвет
        toolbar.setTitleTextAppearance(requireContext(), R.style.ToolbarTitleStyle)  // Применяем стиль


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
            chatAdapter.addMessage(message, currentUserId)
            messageEditText.text.clear()

            recyclerView.scrollToPosition(chatAdapter.itemCount - 1) // Автопрокрутка вниз
        }
    }

    private fun loadChatAndUser(chatId: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")

        lifecycleScope.launch {
            try {
                val chatResponse = RetrofitClient.api.getChatById(token, chatId)
                if (chatResponse.isSuccessful) {
                    val chat = chatResponse.body()
                    chat?.let {
                        val otherUserId = if (it.participantOneId == currentUserId) it.participantTwoId else it.participantOneId

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
        fun newInstance(chatId: Long): ChatFragment {
            val fragment = ChatFragment()
            val args = Bundle()
            args.putLong("chatId", chatId)
            fragment.arguments = args
            return fragment
        }
    }
}
