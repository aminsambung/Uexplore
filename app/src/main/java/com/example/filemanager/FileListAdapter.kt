package com.example.filemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileListAdapter(
    private var items: List<FileItem>,
    private val onClick: (FileItem) -> Unit,
    private val onMore: (FileItem, View) -> Unit
) : RecyclerView.Adapter<FileListAdapter.Holder>() {

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: TextView = v.findViewById(R.id.fileIcon)
        val name: TextView = v.findViewById(R.id.fileName)
        val info: TextView = v.findViewById(R.id.fileInfo)
        val more: ImageButton = v.findViewById(R.id.fileMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        val file = File(item.path)
        holder.icon.text = FileUtils.iconFor(file)
        holder.name.text = item.name
        holder.info.text = if (item.isDirectory) "Folder" else FileUtils.formatSize(item.size)
        holder.itemView.setOnClickListener { onClick(item) }
        holder.more.setOnClickListener { onMore(item, it) }
    }

    fun submit(list: List<FileItem>) {
        items = list
        notifyDataSetChanged()
    }
}
