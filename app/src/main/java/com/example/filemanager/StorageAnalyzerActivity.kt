package com.example.filemanager

import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StorageAnalyzerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage_analyzer)

        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val total = stat.totalBytes
        val free = stat.availableBytes
        val used = total - free
        findViewById<TextView>(R.id.storageSummary).text =
            "Used: ${FileUtils.formatSize(used)}\nFree: ${FileUtils.formatSize(free)}\nTotal: ${FileUtils.formatSize(total)}"
    }
}
