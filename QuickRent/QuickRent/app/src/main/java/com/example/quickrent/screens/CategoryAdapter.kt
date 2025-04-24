package com.example.quickrent.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quickrent.R
import com.example.quickrent.data.model.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val categoryIcon: ImageView = view.findViewById(R.id.categoryIcon) // МІНЕ ОСЫ ЖАҢА ҚОСЫЛДЫ
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.name
        holder.itemView.setOnClickListener { onItemClick(category) }

        // Егер әр категорияның өз иконкасы болса, осылай орнатасыз:
        holder.categoryIcon.setImageResource(category.iconResId)
    }

    override fun getItemCount(): Int = categories.size

    fun updateList(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }
}
