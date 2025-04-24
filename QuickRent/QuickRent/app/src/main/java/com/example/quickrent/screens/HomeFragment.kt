package com.example.quickrent.screens
import android.content.Intent


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickrent.R
import com.example.quickrent.adapters.CategoryAdapter
import com.example.quickrent.data.model.Category
import java.text.Normalizer
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.categoryRecyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)

        // Настройка RecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // Список категорий
        val categories = listOf(
            Category(1, "Электроника", R.drawable.ic_electronics),
            Category(2, "Одежда", R.drawable.ic_clothing),
            Category(3, "Спорт", R.drawable.ic_sports),
            Category(4, "Инструменты", R.drawable.ic_tools),
            Category(5, "Мебель", R.drawable.ic_furniture),
            Category(6, "Игрушки", R.drawable.ic_toys),
            Category(7, "Книги", R.drawable.ic_books),
            Category(8, "Транспорт", R.drawable.ic_transport),
            Category(9, "Другое", R.drawable.ic_other)
        )

        // Создаем адаптер
        categoryAdapter = CategoryAdapter(categories) { category ->
            // Перейти на детальную страницу категории
        }


        recyclerView.adapter = categoryAdapter
        categoryAdapter = CategoryAdapter(categories) { category ->
            val intent = Intent(requireContext(), CategoryDetailActivity::class.java)
            intent.putExtra("CATEGORY_NAME", category.name)
            startActivity(intent)
        }


        // Слушатель для фильтрации категорий по вводу текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(charSequence: Editable?) {
                val query = charSequence.toString().lowercase(Locale.getDefault())
                val filteredCategories = categories.filter {
                    // Нормализуем строки для сравнения
                    normalizeString(it.name).contains(normalizeString(query), ignoreCase = true)
                }
                categoryAdapter.updateList(filteredCategories)
            }
        })

        return view
    }

    // Функция для нормализации строк и приведения их к нижнему регистру
    private fun normalizeString(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "") // Убираем диакритические знаки
            .lowercase(Locale.getDefault()) // Переводим в нижний регистр
    }
}
