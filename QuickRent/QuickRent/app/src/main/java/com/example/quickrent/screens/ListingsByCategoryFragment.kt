package com.example.quickrent.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickrent.R
import com.example.quickrent.adapter.ListingAdapter
import com.example.quickrent.data.model.ListingDTO
import com.example.quickrent.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListingsByCategoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var listingAdapter: ListingAdapter
    private var categoryId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryId = arguments?.getLong("categoryId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_listings_by_category, container, false)
        recyclerView = view.findViewById(R.id.listingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadListings()
    }

    private fun loadListings() {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")

        if (token == "Bearer ") {
            Toast.makeText(requireContext(), "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.api.getListingsByCategory(token, categoryId)
            .enqueue(object : Callback<List<ListingDTO>> {
                override fun onResponse(call: Call<List<ListingDTO>>, response: Response<List<ListingDTO>>) {
                    if (response.isSuccessful) {
                        val listings = response.body() ?: emptyList()
                        listingAdapter = ListingAdapter(listings) { listing ->
                            openListingDetails(listing) // Открываем подробности объявления при клике
                        }
                        recyclerView.adapter = listingAdapter
                    } else {
                        Log.e("Listing", "Ошибка загрузки: ${response.message()}")
                        Toast.makeText(requireContext(), "Ошибка загрузки объявлений", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<ListingDTO>>, t: Throwable) {
                    Log.e("Listing", "Ошибка: ${t.message}")
                    Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun openListingDetails(listing: ListingDTO) {
        val fragment = ListingDetailsFragment.newInstance(listing)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Убедись, что у тебя есть контейнер с таким id
            .addToBackStack(null)
            .commit()
    }
}
