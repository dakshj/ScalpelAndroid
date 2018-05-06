package com.daksh.scalpelandroid.screens.carve

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.daksh.scalpelandroid.R
import com.jakewharton.rxbinding2.view.clicks
import java.io.File

class CarvedFilesListAdapter(
        private val list: MutableList<String> = mutableListOf(),
        private val viewModel: CarveViewModel
) :
        RecyclerView.Adapter<CarvedFilesListAdapter.CarvedFilesViewHolder>() {

    class CarvedFilesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textFileName: TextView = itemView.findViewById(R.id.textFileName)
        val textFilePath: TextView = itemView.findViewById(R.id.textFilePath)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarvedFilesViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.carved_files_list_item, parent, false)
        return CarvedFilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarvedFilesViewHolder, position: Int) {
        val item = File(list[position])

        holder.textFileName.text = item.name
        holder.textFilePath.text = item.absolutePath

        holder.itemView.clicks().subscribe {
            viewModel.fileClicked()
        }
    }

    override fun getItemCount() = list.size

    fun updateList(newList: List<String>) {
        val diffResult = DiffUtil.calculateDiff(CarvedFilesDiffUtilCallback(list, newList))
        list.clear()
        list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}
