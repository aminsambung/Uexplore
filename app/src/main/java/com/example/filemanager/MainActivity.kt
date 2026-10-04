package com.example.uxplore // ⚠️ SESUAIKAN DENGAN NAMA PACKAGE ANDA

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video) // ⚠️ Kita pakai layout Video

        val rvVideos = findViewById<RecyclerView>(R.id.rvVideos)
        
        // Menggunakan GridLayoutManager dengan 2 kolom
        rvVideos.layoutManager = GridLayoutManager(this, 2)

        // Cek Izin
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            // Minta izin MANAGE_EXTERNAL_STORAGE (kode lengkapnya ada di jawaban sebelumnya)
        } else {
            // Ambil daftar video dari penyimpanan
            val videoFiles = getVideoFiles()
            val adapter = VideoAdapter(videoFiles) { file ->
                // Aksi saat video diklik
            }
            rvVideos.adapter = adapter
        }
    }

    // Fungsi untuk mencari semua file video di HP
    private fun getVideoFiles(): List<File> {
        val videoList = mutableListOf<File>()
        val root = Environment.getExternalStorageDirectory()
        val extensions = arrayOf("mp4", "mkv", "avi", "3gp", "webm")

        fun searchVideos(dir: File) {
            val files = dir.listFiles() ?: return
            for (file in files) {
                if (file.isDirectory) {
                    searchVideos(file)
                } else {
                    if (extensions.any { file.extension.equals(it, ignoreCase = true) }) {
                        videoList.add(file)
                    }
                }
            }
        }
        searchVideos(root)
        return videoList
    }
}
