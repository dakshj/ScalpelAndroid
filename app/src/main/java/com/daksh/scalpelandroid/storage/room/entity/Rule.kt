package com.daksh.scalpelandroid.storage.room.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Rule(

        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,

        val extension: String,

        val minBytesAmount: Long = 0,

        val maxBytesAmount: Long,

        val headerBytes: String,

        val footerBytes: String? = null,

        val bytesCaseSensitive: Boolean = true,

        val reverseSearchFooter: Boolean = false,

        val skipFooter: Boolean = false
) {
    companion object {
        const val WILDCARD = "?"

        // TODO Continue here:
        // https://github.com/sleuthkit/scalpel/blob/master/scalpel.conf#L21
    }
}
