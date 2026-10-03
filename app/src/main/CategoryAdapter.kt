package com.example.filemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView

class CategoryAdapter(private val onClick: (String) -> Unit) {
    fun add(container: GridLayout, name: String, icon: String) {
        val view = LayoutInflater.from(container.context)
            .inflate(com.example.filemanager.R.layout.item_category, container, false)
        view.findViewById<TextView>(R.id.categoryName).text = name
        view.findViewById<TextView>(R.id.categoryIcon).text = icon
        view.setOnClickListener { onClick(name) }
        container.addView(view)
    }
}
