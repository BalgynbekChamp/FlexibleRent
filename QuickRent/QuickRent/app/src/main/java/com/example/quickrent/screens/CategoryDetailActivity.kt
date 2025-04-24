package com.example.quickrent.screens
import android.view.View

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quickrent.R

class CategoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        val categoryName = intent.getStringExtra("CATEGORY_NAME")

        val titleTextView = findViewById<TextView>(R.id.categoryTitle)
        val emptyMessage = findViewById<TextView>(R.id.emptyMessage)

        titleTextView.text = categoryName

        // Мұнда болашақта нақты заттар қосуға болады.
        // Әзірше тек бос хабарлама көрсетеміз.
        emptyMessage.visibility = View.VISIBLE
    }
}
