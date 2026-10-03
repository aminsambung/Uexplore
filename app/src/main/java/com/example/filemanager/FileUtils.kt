package com.example.filemanager

import java.io.File

object FileUtils {
    fun formatSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val units = arrayOf("KB", "MB", "GB", "TB")
        var value = bytes.toDouble()
        var index = -1
        while (value >= 1024 && index < units.lastIndex) {
            value /= 1024
            index++
        }
        return String.format("%.1f %s", value, units[index])
    }

    fun iconFor(file: File): String {
        if (file.isDirectory) return "📁"
        return when (file.extension.lowercase()) {
            "jpg", "jpeg", "png", "webp", "gif" -> "🖼️"
            "mp4", "mkv", "mov", "avi" -> "🎬"
            "mp3", "wav", "flac", "m4a" -> "🎵"
            "pdf", "doc", "docx", "txt", "xls", "xlsx", "ppt", "pptx" -> "📄"
            "zip", "rar", "7z", "tar", "gz" -> "📦"
            "apk", "aab" -> "📱"
            else -> "📄"
        }
    }
}
