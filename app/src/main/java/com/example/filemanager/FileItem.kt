package com.example.filemanager

data class FileItem(
    val name: String,
    val path: String,
    val size: Long,
    val isDirectory: Boolean
)
