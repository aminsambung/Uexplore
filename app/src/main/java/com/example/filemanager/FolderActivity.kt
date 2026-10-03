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
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val path = intent.getStringExtra("path") ?: Environment.getExternalStorageDirectory().absolutePath
        findViewById<TextView>(R.id.pathText).text = path

        val files = File(path).listFiles()?.sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() })
            ?.map { FileItem(it.name, it.absolutePath, if (it.isFile) it.length() else 0, it.isDirectory) }
            ?: emptyList()

        val recycler = findViewById<RecyclerView>(R.id.folderRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = FileListAdapter(files, { item ->
            if (item.isDirectory) startActivity(Intent(this, FolderActivity::class.java).putExtra("path", item.path))
            else if (item.name.lowercase().matches(Regex(".*\\.(jpg|jpeg|png|webp|gif)$")))
                startActivity(Intent(this, PreviewActivity::class.java).putExtra("path", item.path))
        }, { item, anchor ->
            val p = android.widget.PopupMenu(this, anchor)
            p.menu.add("Share")
            p.menu.add("Rename")
            p.menu.add("Delete")
            p.menu.add("Details")
            p.show()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}
