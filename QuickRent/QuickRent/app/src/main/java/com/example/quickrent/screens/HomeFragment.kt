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
import com.example.quickrent.data.model.CategoryDTO
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
                    val categories = response.body() ?: emptyList()
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
                    Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CategoryDTO>>, t: Throwable) {
                Log.e("Category", "Ошибка загрузки категорий: ${t.message}")
                Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openListingsByCategory(categoryId: Long) {
        Log.d("HomeFragment", "Opening listings for category with ID: $categoryId")
        val fragment = ListingsByCategoryFragment().apply {
            arguments = Bundle().apply {
                putLong("categoryId", categoryId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Убедись что у тебя есть контейнер с таким id
            .addToBackStack(null)
            .commit()
    }


}
