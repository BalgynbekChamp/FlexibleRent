package com.example.quickrent.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quickrent.R
import com.example.quickrent.data.model.ListingDTO


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
        // Открываем чат с арендодателем
        val chatFragment = ChatFragment.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, chatFragment)
            .addToBackStack(null)
            .commit()
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
