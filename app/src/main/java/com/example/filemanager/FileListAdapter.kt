package com.example.filemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileListAdapter(
    private val items: List<File>,
    private val onClick: (File) -> Unit,
    private val onMore: (File, View) -> Unit
) : RecyclerView.Adapter<FileListAdapter.Holder>() {

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.ivIcon)
        val name: TextView = v.findViewById(R.id.tvName)
        val date: TextView = v.findViewById(R.id.tvDate)
        val options: ImageView = v.findViewById(R.id.btnOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val file = items[position]

        // Nama File/Folder
        holder.name.text = file.name

        // Format Tanggal (contoh: "26 Apr")
        val sdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        holder.date.text = sdf.format(Date(file.lastModified()))

        // Ikon (Folder atau File)
        if (file.isDirectory) {
            holder.icon.setImageResource(android.R.drawable.ic_menu_gallery)
        } else {
            holder.icon.setImageResource(android.R.drawable.ic_menu_edit)
        }

        // Klik pada item
        holder.itemView.setOnClickListener { onClick(file) }

        // Klik pada tombol opsi (titik tiga)
        holder.options.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Pilih")
            popup.menu.add("Quick Share")
            popup.menu.add("Pindahkan ke")
            popup.menu.add("Salin ke")
            popup.menu.add("Ganti nama")
            popup.menu.add("Kompresi")
            popup.menu.add("Hapus secara permanen")
            popup.menu.add("Info folder")

            popup.setOnMenuItemClickListener { item ->
                onMore(file, view) // Memanggil fungsi callback
                true
            }
            popup.show()
        }
    }
}
