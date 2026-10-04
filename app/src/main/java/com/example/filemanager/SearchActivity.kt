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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val input = findViewById<EditText>(R.id.searchInput)
        recycler = findViewById(R.id.searchRecycler)
        empty = findViewById(R.id.emptyText)

        recycler.layoutManager = LinearLayoutManager(this)

        input.requestFocus()
        input.setOnEditorActionListener { _, _, _ ->
            search(input.text.toString())
            true
        }
    }

    private fun search(query: String) {
        if (query.isBlank()) return

        // Cari file, hasilnya langsung berupa List<File>
        val results = mutableListOf<File>()
        walk(Environment.getExternalStorageDirectory(), query.lowercase(), results, 0)

        // Buat adapter baru dengan hasil pencarian
        val adapter = FileListAdapter(
            items = results.take(100),
            onClick = { file ->
                // Aksi saat file diklik (bisa disesuaikan)
            },
            onMore = { file, anchor ->
                // Aksi saat titik tiga diklik
            }
        )
        recycler.adapter = adapter

        empty.text = if (results.isEmpty()) "No files found" else "${results.size} result(s)"
    }

    private fun walk(dir: File, query: String, out: MutableList<File>, depth: Int) {
        if (depth > 6 || out.size >= 100) return
        val children = dir.listFiles() ?: return
        for (f in children) {
            // Lewati file/folder tersembunyi
            if (f.name.startsWith(".")) continue

            if (f.name.lowercase().contains(query)) {
                out.add(f) // Langsung tambahkan File, bukan FileItem
            }
            if (f.isDirectory && !f.isHidden) walk(f, query, out, depth + 1)
        }
    }
}
