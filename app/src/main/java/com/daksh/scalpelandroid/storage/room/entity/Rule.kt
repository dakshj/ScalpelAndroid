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

        /**
         * Stores header bytes in the Scalpel format.
         *
         * *Does not* support Octal and regex as of now; so only plain text, hex, and wildcards.
         */
        val header: String,

        /**
         * Stores header bytes in the Scalpel format.
         *
         * *Does not* support Octal and regex as of now; so only plain text, hex, and wildcards.
         */
        val footer: String? = null,

        val bytesCaseSensitive: Boolean = true,

        val reverseSearchFooter: Boolean = false,

        val skipFooter: Boolean = false,

        /**
         * Saves the carving even if a footer match was not found
         */
        val forceSave: Boolean = false
) {
    companion object {
        const val WILDCARD: Char = '?'
    }
}
