package com.daksh.scalpelandroid.screens.carve

import android.support.v7.util.DiffUtil

class CarvedFilesDiffUtilCallback(private val oldList: List<String>,
        private val newList: List<String>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            areItemsTheSame(oldItemPosition, newItemPosition)

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size
}
