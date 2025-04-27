package com.example.quickrent.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.quickrent.R

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

    private fun sendMessage() {
        val message = messageEditText.text.toString()
        if (message.isNotEmpty()) {
            messages.add(message)
            messageEditText.text.clear()

            // Обновляем список сообщений
            (messagesListView.adapter as ArrayAdapter<String>).notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
}
