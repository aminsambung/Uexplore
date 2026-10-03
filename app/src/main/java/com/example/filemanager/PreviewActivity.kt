package com.example.filemanager

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class PreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val path = intent.getStringExtra("path") ?: return
        val file = File(path)
        findViewById<TextView>(R.id.previewName).text = file.name
        findViewById<TextView>(R.id.previewInfo).text = FileUtils.formatSize(file.length())

        val image = findViewById<ImageView>(R.id.previewImage)
        image.setImageBitmap(BitmapFactory.decodeFile(path))

        findViewById<View>(R.id.btnWallpaper).setOnClickListener {
            setWallpaper(path)
        }
    }

    private fun setWallpaper(path: String) {
        try {
            val bitmap = BitmapFactory.decodeFile(path) ?: return
            val manager = WallpaperManager.getInstance(this)
            manager.setBitmap(bitmap)
            Toast.makeText(this, "Wallpaper applied", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to set wallpaper", Toast.LENGTH_LONG).show()
        }
    }
}
