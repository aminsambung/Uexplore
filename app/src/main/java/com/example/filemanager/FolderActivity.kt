package com.example.filemanager

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        
        // Setup Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Ambil path dari intent
        val path = intent.getStringExtra("path") ?: Environment.getExternalStorageDirectory().absolutePath
        findViewById<TextView>(R.id.pathText).text = path

        // Baca daftar file, urutkan: Folder dulu, lalu File, berdasarkan nama
        val files = File(path).listFiles()
            ?.filter { !it.name.startsWith(".") } // Sembunyikan file tersembunyi
            ?.sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() })
            ?: emptyList()

        // Setup RecyclerView
        val recycler = findViewById<RecyclerView>(R.id.folderRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        
        // Gunakan FileListAdapter yang baru (menerima List<File>)
        recycler.adapter = FileListAdapter(
            items = files,
            onClick = { file ->
                if (file.isDirectory) {
                    // Jika folder, buka FolderActivity lagi dengan path baru
                    startActivity(Intent(this, FolderActivity::class.java).putExtra("path", file.absolutePath))
                } else if (file.name.lowercase().matches(Regex(".*\\.(jpg|jpeg|png|webp|gif)$"))) {
                    // Jika gambar, buka PreviewActivity
                    startActivity(Intent(this, PreviewActivity::class.java).putExtra("path", file.absolutePath))
                }
            },
            onMore = { file, anchor ->
                // Menu Popup saat titik tiga diklik
                val popup = android.widget.PopupMenu(this, anchor)
                popup.menu.add("Share")
                popup.menu.add("Rename")
                popup.menu.add("Delete")
                popup.menu.add("Details")
                popup.show()
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}
