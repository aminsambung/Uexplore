package com.example.filemanager

import android.os.Bundle
import android.os.Environment
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class SearchActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var empty: TextView
    private lateinit var adapter: FileListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val input = findViewById<EditText>(R.id.searchInput)
        recycler = findViewById(R.id.searchRecycler)
        empty = findViewById(R.id.emptyText)

        adapter = FileListAdapter(emptyList(), {}, { _, _ -> })
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        input.requestFocus()
        input.setOnEditorActionListener { _, _, _ ->
            search(input.text.toString())
            true
        }
    }

    private fun search(query: String) {
        if (query.isBlank()) return
        val results = mutableListOf<FileItem>()
        walk(Environment.getExternalStorageDirectory(), query.lowercase(), results, 0)
        adapter.submit(results.take(100))
        empty.text = if (results.isEmpty()) "No files found" else "${results.size} result(s)"
    }

    private fun walk(dir: File, query: String, out: MutableList<FileItem>, depth: Int) {
        if (depth > 6 || out.size >= 100) return
        val children = dir.listFiles() ?: return
        for (f in children) {
            if (f.name.lowercase().contains(query)) {
                out.add(FileItem(f.name, f.absolutePath, if (f.isFile) f.length() else 0, f.isDirectory))
            }
            if (f.isDirectory && !f.isHidden) walk(f, query, out, depth + 1)
        }
    }
}
