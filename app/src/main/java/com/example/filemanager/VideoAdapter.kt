package com.example.uxplore // ⚠️ SESUAIKAN DENGAN NAMA PACKAGE ANDA

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import java.text.DecimalFormat

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

        // 1. Memuat Thumbnail Video menggunakan Glide
        Glide.with(holder.itemView.context)
            .load(videoFile)
            .into(holder.thumbnail)

        // 2. Menampilkan Ukuran File
        holder.size.text = formatFileSize(videoFile.length())

        holder.itemView.setOnClickListener { onClick(videoFile) }
    }

    override fun getItemCount(): Int = videoList.size

    // Fungsi untuk mengubah byte menjadi format MB/KB
    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.##").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }
}
