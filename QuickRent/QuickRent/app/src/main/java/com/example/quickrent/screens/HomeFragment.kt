package com.example.quickrent.screens
import androidx.recyclerview.widget.GridLayoutManager

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickrent.R
import com.example.quickrent.adapter.CategoryAdapter
import com.example.quickrent.data.model.CategoryDTO
import com.example.quickrent.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import com.example.quickrent.adapter.ListingAdapter
import com.example.quickrent.data.model.ListingDTO


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val apiService = RetrofitClient.api // или как у тебя называется клиент
    private var categories: List<CategoryDTO> = emptyList()
    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var popularAdapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.categoryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // Настроим EditText для поиска
        val searchEditText: EditText = view.findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterCategories(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popularRecyclerView = view.findViewById(R.id.popularRecyclerView)
        popularRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCategories(null) // Загрузим главные категории
        loadPopularListings()
    }

    private fun loadCategories(parentId: Long?) {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")

        if (token == "Bearer ") {
            Log.e("Authorization", "Missing token")
            Toast.makeText(requireContext(), "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
            return
        }

        val call = if (parentId == null)
            RetrofitClient.api.getMainCategories(token)
        else
            RetrofitClient.api.getSubcategories(token, parentId)

        call.enqueue(object : Callback<List<CategoryDTO>> {
            override fun onResponse(
                call: Call<List<CategoryDTO>>,
                response: Response<List<CategoryDTO>>
            ) {
                if (response.isSuccessful) {
                    categories = response.body() ?: emptyList()
                    categoryAdapter = CategoryAdapter(categories) { clickedCategory ->
                        Log.d("HomeFragment", "Clicked category: ${clickedCategory.name}, parentId: ${clickedCategory.parentId}")
                        if (clickedCategory.parentId != null) {
                            openListingsByCategory(clickedCategory.id)
                        } else {
                            loadCategories(clickedCategory.id)
                        }
                    }
                    recyclerView.adapter = categoryAdapter
                } else {
                    Log.e("Category", "Ошибка загрузки категорий: ${response.message()}")
                    Toast.makeText(requireContext(), "Ошибка загрузки111", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CategoryDTO>>, t: Throwable) {
                Log.e("Category", "Ошибка загрузки категорий: ${t.message}")
                Toast.makeText(requireContext(), "Ошибка загрузки222", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadPopularListings() {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")

        // Логируем токен перед отправкой запроса
        Log.d("Popular", "Sending request with token: $token")

        RetrofitClient.api.getPopularListings(token).enqueue(object : Callback<List<ListingDTO>> {
            override fun onResponse(call: Call<List<ListingDTO>>, response: Response<List<ListingDTO>>) {
                if (response.isSuccessful) {
                    val listings = response.body() ?: emptyList()

                    // Логируем успешный ответ с данными
                    Log.d("Popular", "Successfully loaded popular listings: ${listings.size} items")

                    popularAdapter = ListingAdapter(listings) { selected ->
                        openListingDetails(selected.id!!)
                    }
                    popularRecyclerView.adapter = popularAdapter
                } else {
                    Log.e("Popular", "Ошибка загрузки: ${response.code()} ${response.message()}")
                    Toast.makeText(requireContext(), "Не удалось загрузить популярные", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ListingDTO>>, t: Throwable) {
                Log.e("Popular", "Ошибка: ${t.message}")
                Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openListingDetails(listingId: Long) {
        if (listingId == -1L) {
            Toast.makeText(requireContext(), "Некорректный ID объявления", Toast.LENGTH_SHORT).show()
            return
        }

        val fragment = ListingDetailsFragment().apply {
            arguments = Bundle().apply {
                putLong("listingId", listingId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun filterCategories(query: String) {
        val filteredCategories = categories.filter {
            it.name.contains(query, ignoreCase = true)
        }
        categoryAdapter = CategoryAdapter(filteredCategories) { clickedCategory ->
            Log.d("HomeFragment", "Clicked category: ${clickedCategory.name}, parentId: ${clickedCategory.parentId}")
            if (clickedCategory.parentId != null) {
                openListingsByCategory(clickedCategory.id)
            } else {
                loadCategories(clickedCategory.id)
            }
        }
        recyclerView.adapter = categoryAdapter
    }

    private fun openListingsByCategory(categoryId: Long) {
        Log.d("HomeFragment", "Opening listings for category with ID: $categoryId")
        val fragment = ListingsByCategoryFragment().apply {
            arguments = Bundle().apply {
                putLong("categoryId", categoryId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
