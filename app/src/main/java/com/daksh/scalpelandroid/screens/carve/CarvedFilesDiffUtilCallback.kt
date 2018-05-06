package com.daksh.scalpelandroid.screens.carve

import android.support.v7.util.DiffUtil
import java.io.File

class CarvedFilesDiffUtilCallback(private val oldList: List<File>,
        private val newList: List<File>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            areItemsTheSame(oldItemPosition, newItemPosition)

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size
}
