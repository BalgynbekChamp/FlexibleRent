package com.example.quickrent.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickrent.R
import com.example.quickrent.data.model.ChatDTO
import com.example.quickrent.data.model.ChatDisplayModel
import com.example.quickrent.data.model.UserDTO

class ChatListAdapter(
    private val chats: List<ChatDisplayModel>,
    private val onChatClick: (ChatDisplayModel) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val phoneText: TextView = itemView.findViewById(R.id.phoneText)
        val createdAtText: TextView = itemView.findViewById(R.id.createdAtText)
        val avatarImage: ImageView = itemView.findViewById(R.id.avatarImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.nameText.text = chat.otherUserName
        holder.phoneText.text = chat.otherUserPhone ?: "Телефон скрыт"
        holder.createdAtText.text = chat.createdAt

        // Если используешь Glide или Coil для загрузки фото
        chat.otherUserAvatarUrl?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .into(holder.avatarImage)
        }

        holder.itemView.setOnClickListener {
            onChatClick(chat)
        }
    }

    override fun getItemCount(): Int = chats.size
}
