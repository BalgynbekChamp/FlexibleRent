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
import com.example.quickrent.data.model.ListingDTO

class ListingAdapter(
    private val listings: List<ListingDTO>,
    private val onItemClick: (ListingDTO) -> Unit // Обработчик клика
) : RecyclerView.Adapter<ListingAdapter.ListingViewHolder>() {

    class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.listingTitleTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.listingPriceTextView)
        val imageView: ImageView = itemView.findViewById(R.id.listingImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listing, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val listing = listings[position]
        holder.titleTextView.text = listing.title
        holder.priceTextView.text = "${listing.price} ₸"

        // Берем первое фото из списка photos
        val baseUrl = "http://10.0.2.2:8080" // Эмулятор Android Studio
        val imageUrl = listing.photos.firstOrNull()?.filePath?.let { baseUrl + it } ?: ""

        Log.d("ListingAdapter", "Загружаем фото: $imageUrl")
        Glide.with(holder.itemView.context)
            .load(imageUrl) // если imageUrl пустое, ничего не покажется
            .placeholder(R.drawable.ic_placeholder) // картинка-заглушка
            .into(holder.imageView)

        // Обработчик клика на элемент
        holder.itemView.setOnClickListener {
            onItemClick(listing) // передаем выбранное объявление
        }
    }

    override fun getItemCount(): Int = listings.size
}
