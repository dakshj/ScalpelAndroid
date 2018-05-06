package com.daksh.scalpelandroid.storage.room.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Rule(

        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,

        val extension: String,

        val minBytesAmount: Int = 0,

        val maxBytesAmount: Int,

        val header: String,

        val footer: String? = null,

        val bytesCaseSensitive: Boolean = true,

        val reverseSearchFooter: Boolean = false,

        val skipFooter: Boolean = false
) {
    companion object {
        const val WILDCARD: Char = '?'
    }
}
