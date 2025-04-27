package com.example.quickrent.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        Glide.with(holder.itemView.context)
            .load(category.iconUrl ?: "") // если iconUrl null, просто ничего не покажет
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.iconImageView)

        holder.itemView.setOnClickListener {
            Log.d("CategoryAdapter", "Clicked category: ${category.name}, ID: ${category.id}")
            onItemClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}
