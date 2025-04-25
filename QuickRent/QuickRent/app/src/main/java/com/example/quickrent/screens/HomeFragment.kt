package com.example.quickrent.screens



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
import com.example.quickrent.adapter.CategoryAdapter
import com.example.quickrent.model.CategoryDto
import com.example.quickrent.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val apiService = RetrofitClient.api // или как у тебя называется клиент

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.categoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCategories(null) // Загрузим главные категории
    }

    private fun loadCategories(parentId: Long?) {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")

        // Проверяем, что токен не пустой
        if (token == "Bearer ") {
            Log.e("Authorization", "Missing token")
            Toast.makeText(requireContext(), "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
            return
        }

        val call = if (parentId == null)
            RetrofitClient.api.getMainCategories(token)  // передаем токен для главных категорий
        else
            RetrofitClient.api.getSubcategories(token, parentId)  // передаем токен для подкатегорий

        call.enqueue(object : Callback<List<CategoryDto>> {
            override fun onResponse(
                call: Call<List<CategoryDto>>,
                response: Response<List<CategoryDto>>
            ) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    // передаем в адаптер и отображаем
                    categoryAdapter = CategoryAdapter(categories) { clickedCategory ->
                        loadCategories(clickedCategory.id) // загружаем подкатегории при клике
                    }
                    recyclerView.adapter = categoryAdapter
                } else {
                    Log.e("Category", "Ошибка загрузки категорий: ${response.message()}")
                    Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CategoryDto>>, t: Throwable) {
                Log.e("Category", "Ошибка загрузки категорий: ${t.message}")
                Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
