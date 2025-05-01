package com.example.quickrent.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.quickrent.R
import com.example.quickrent.data.model.CategoryDTO

class CategoryAdapter(
    private val categories: List<CategoryDTO>,
    private val onItemClick: (CategoryDTO) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.nameTextView.text = category.name

        val baseUrl = "http://10.0.2.2:8080"
        val fullUrl = baseUrl + (category.iconUrl ?: "")

        val sharedPreferences = holder.itemView.context.getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", "") ?: ""

        Log.d("Glide", "Загружается URL: $fullUrl с токеном")

        val glideUrl = GlideUrl(
            fullUrl,
            LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        )

        Glide.with(holder.itemView.context)
            .load(glideUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .into(holder.iconImageView)

        holder.itemView.setOnClickListener {
            Log.d("CategoryAdapter", "Clicked category: ${category.name}, ID: ${category.id}")
            onItemClick(category)
        }
    }


    override fun getItemCount(): Int = categories.size
}
