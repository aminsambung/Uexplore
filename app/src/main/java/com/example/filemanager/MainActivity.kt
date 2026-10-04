package com.example.uxplore // ⚠️ SESUAIKAN DENGAN NAMA PACKAGE ANDA

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fileAdapter: FileAdapter
    private val fileList = mutableListOf<File>()
    private var currentPath: String = Environment.getExternalStorageDirectory().absolutePath

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) loadFiles(currentPath) 
        else Toast.makeText(this, "Izin ditolak!", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rvFiles)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fileAdapter = FileAdapter(fileList) { file ->
            if (file.isDirectory) {
                currentPath = file.absolutePath
                loadFiles(currentPath)
            } else {
                Toast.makeText(this, "File: ${file.name}", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = fileAdapter

        checkPermissions()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } else {
                loadFiles(currentPath)
            }
        } else {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            val notGranted = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (notGranted.isNotEmpty()) requestPermissionLauncher.launch(notGranted.toTypedArray())
            else loadFiles(currentPath)
        }
    }

    private fun loadFiles(path: String) {
        try {
            val directory = File(path)
            val files = directory.listFiles()
            fileList.clear()

            if (files != null) {
                val folders = files.filter { it.isDirectory }.sortedBy { it.name.lowercase() }
                val normalFiles = files.filter { it.isFile }.sortedBy { it.name.lowercase() }
                fileList.addAll(folders)
                fileList.addAll(normalFiles)
            }
            fileAdapter.notifyDataSetChanged()
            findViewById<TextView>(R.id.tvTitle).text = directory.name

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val parent = File(currentPath).parentFile
        if (parent != null && parent.exists() && currentPath != Environment.getExternalStorageDirectory().absolutePath) {
            currentPath = parent.absolutePath
            loadFiles(currentPath)
        } else {
            super.onBackPressed()
        }
    }
}

// ================== ADAPTER ==================
class FileAdapter(
    private val files: List<File>,
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.ivIcon)
        val name: TextView = view.findViewById(R.id.tvName)
        val date: TextView = view.findViewById(R.id.tvDate)
        val options: ImageView = view.findViewById(R.id.btnOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.name.text = file.name

        // Format Tanggal (Contoh: 26 Apr)
        val sdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        val dateString = sdf.format(Date(file.lastModified()))
        holder.date.text = dateString

        // Ikon (Sementara pakai folder untuk semua, nanti bisa dikembangkan)
        if (file.isDirectory) {
            holder.icon.setImageResource(android.R.drawable.ic_menu_gallery)
        } else {
            holder.icon.setImageResource(android.R.drawable.ic_menu_edit)
        }

        holder.itemView.setOnClickListener { onClick(file) }
        holder.options.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Opsi: ${file.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = files.size
}
