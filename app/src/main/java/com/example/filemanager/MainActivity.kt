package com.example.filemanager // ⚠️ SESUAIKAN DENGAN NAMA PACKAGE ANDA

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    // Variabel untuk Halaman Penyimpanan Internal
    private lateinit var rvFiles: RecyclerView
    private lateinit var fileAdapter: FileListAdapter
    private val fileList = mutableListOf<File>()
    private var currentPath: String = Environment.getExternalStorageDirectory().absolutePath

    // Launcher Izin
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            loadInitialData()
        } else {
            Toast.makeText(this, "Izin penyimpanan ditolak!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ==========================================
        // SKENARIO: PENYIMPANAN INTERNAL (AKTIF)
        // ==========================================
        setContentView(R.layout.activity_main)
        initStorageViews()

        // Cek Izin
        checkPermissions()
    }

    // ================== INISIALISASI TAMPILAN ==================

    private fun initStorageViews() {
        rvFiles = findViewById(R.id.rvFiles)
        rvFiles.layoutManager = LinearLayoutManager(this)
        fileAdapter = FileListAdapter(
            items = fileList,
            onClick = { file ->
                if (file.isDirectory) {
                    currentPath = file.absolutePath
                    loadFiles(currentPath)
                } else {
                    Toast.makeText(this, "File: ${file.name}", Toast.LENGTH_SHORT).show()
                }
            },
            onMore = { file, view ->
                Toast.makeText(this, "Opsi: ${file.name}", Toast.LENGTH_SHORT).show()
            }
        )
        rvFiles.adapter = fileAdapter
    }

    // ================== IZIN & PEMUATAN DATA ==================

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } else {
                loadInitialData()
            }
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val notGranted = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (notGranted.isNotEmpty()) {
                requestPermissionLauncher.launch(notGranted.toTypedArray())
            } else {
                loadInitialData()
            }
        }
    }

    private fun loadInitialData() {
        loadFiles(currentPath)
    }

    // ================== LOGIKA PENYIMPANAN INTERNAL ==================

    private fun loadFiles(path: String) {
        try {
            val directory = File(path)
            val files = directory.listFiles()
            fileList.clear()

            if (files != null) {
                val folders = files.filter { it.isDirectory && !it.name.startsWith(".") }
                    .sortedBy { it.name.lowercase() }
                val normalFiles = files.filter { it.isFile && !it.name.startsWith(".") }
                    .sortedBy { it.name.lowercase() }

                fileList.addAll(folders)
                fileList.addAll(normalFiles)
            }
            fileAdapter.notifyDataSetChanged()
            findViewById<TextView>(R.id.tvTitle)?.text = directory.name

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ================== TOMBOL BACK ==================

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

// ================== ADAPTER: VIDEO ==================
class VideoAdapter(
    private val videoList: List<File>,
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
        val size: TextView = view.findViewById(R.id.tvSize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoFile = videoList[position]
        Glide.with(holder.itemView.context).load(videoFile).into(holder.thumbnail)
        holder.size.text = formatFileSize(videoFile.length())
        holder.itemView.setOnClickListener { onClick(videoFile) }
    }

    override fun getItemCount(): Int = videoList.size
}

// ================== ADAPTER: GAMBAR ==================
class GalleryAdapter(
    private val imageList: List<File>,
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
        val size: TextView = view.findViewById(R.id.tvSize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val imageFile = imageList[position]
        holder.size.text = formatFileSize(imageFile.length())

        val requestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(300, 300)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)

        Glide.with(holder.itemView.context)
            .load(imageFile)
            .apply(requestOptions)
            .into(holder.thumbnail)

        holder.itemView.setOnClickListener { onClick(imageFile) }
    }

    override fun getItemCount(): Int = imageList.size
}

// ================== FUNGSI UTILITAS ==================
fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return DecimalFormat("#,##0.##").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}
