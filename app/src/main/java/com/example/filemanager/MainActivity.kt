package com.example.filemanager

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FileListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btnSearch).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        findViewById<View>(R.id.btnBrowse).setOnClickListener {
            startActivity(Intent(this, FolderActivity::class.java))
        }
        findViewById<View>(R.id.btnFavorites).setOnClickListener {
            android.widget.Toast.makeText(this, "Favorites coming soon", android.widget.Toast.LENGTH_SHORT).show()
        }

        setupCategories()
        setupStorage()
        setupRecent()
    }

    private fun setupCategories() {
        val grid = findViewById<GridLayout>(R.id.categoryGrid)
        val categories = listOf(
            "Photos" to "🖼️",
            "Videos" to "🎬",
            "Audio" to "🎵",
            "Documents" to "📄",
            "Archive" to "📦",
            "APK" to "📱"
        )
        val adapter = CategoryAdapter { name ->
            android.widget.Toast.makeText(this, "$name selected", android.widget.Toast.LENGTH_SHORT).show()
        }
        categories.forEach { adapter.add(grid, it.first, it.second) }
    }

    private fun setupStorage() {
        val stat = android.os.StatFs(Environment.getDataDirectory().path)
        val total = stat.totalBytes
        val free = stat.availableBytes
        val used = total - free
        val percent = if (total > 0) (used * 100 / total).toInt() else 0

        findViewById<ProgressBar>(R.id.storageProgress).progress = percent
        findViewById<TextView>(R.id.storageText).text =
            "${FileUtils.formatSize(used)} used • ${FileUtils.formatSize(total)} total"
    }

    private fun setupRecent() {
        val root = Environment.getExternalStorageDirectory()
        val items = root.listFiles()?.sortedByDescending { it.lastModified() }
            ?.take(12)
            ?.map { FileItem(it.name, it.absolutePath, if (it.isFile) it.length() else 0, it.isDirectory) }
            ?: emptyList()

        adapter = FileListAdapter(
            items,
            onClick = { item ->
                if (item.isDirectory) {
                    startActivity(Intent(this, FolderActivity::class.java).putExtra("path", item.path))
                } else if (item.name.lowercase().matches(Regex(".*\\.(jpg|jpeg|png|webp|gif)$"))) {
                    startActivity(Intent(this, PreviewActivity::class.java).putExtra("path", item.path))
                }
            },
            onMore = { item, view -> showActions(item, view) }
        )
        findViewById<RecyclerView>(R.id.fileRecycler).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.fileRecycler).adapter = adapter
    }

    private fun showActions(item: FileItem, anchor: View) {
        val popup = android.widget.PopupMenu(this, anchor)
        popup.menu.add("Share")
        popup.menu.add("Rename")
        popup.menu.add("Delete")
        popup.menu.add("Details")
        popup.setOnMenuItemClickListener {
            android.widget.Toast.makeText(this, "${it.title}: ${item.name}", android.widget.Toast.LENGTH_SHORT).show()
            true
        }
        popup.show()
    }
}
