package com.daksh.scalpelandroid.screens.carve

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.daksh.scalpelandroid.R
import com.daksh.scalpelandroid.extensions.round
import com.daksh.scalpelandroid.storage.FileOpener
import com.jakewharton.rxbinding2.view.clicks
import java.io.File

class CarvedFilesListAdapter(
        private val list: MutableList<File> = mutableListOf()
) :
        RecyclerView.Adapter<CarvedFilesListAdapter.CarvedFilesViewHolder>() {

    class CarvedFilesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textFileName: TextView = itemView.findViewById(R.id.textFileName)
        val textFilePath: TextView = itemView.findViewById(R.id.textFilePath)
        val textFileSize: TextView = itemView.findViewById(R.id.textFileSize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarvedFilesViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.carved_files_list_item, parent, false)
        return CarvedFilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarvedFilesViewHolder, position: Int) {
        val item = list[position]

        holder.textFileName.text = item.name
        holder.textFilePath.text = String.format("Path:\n%s", item.absolutePath)
        holder.textFileSize.text = String.format("Size:\n%s KB",
                (item.length().toDouble() / 1024).round(decimals = 3))

        holder.itemView.clicks().subscribe {
            FileOpener.open(item, holder.itemView.context)
        }
    }

    override fun getItemCount() = list.size

    fun updateList(newList: List<File>) {
        val diffResult = DiffUtil.calculateDiff(CarvedFilesDiffUtilCallback(list, newList))
        list.clear()
        list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}
