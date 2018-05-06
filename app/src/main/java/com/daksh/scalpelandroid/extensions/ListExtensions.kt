package com.daksh.scalpelandroid.extensions

import com.daksh.scalpelandroid.storage.room.entity.Rule

fun List<Byte>.matchWithWildCard(listWithWildCard: List<Byte>): Boolean {
    if (this.size != listWithWildCard.size) return false

    return this.indices
            .map { this[it] to listWithWildCard[it] }
            .count {
                if (it.second == Rule.WILDCARD.toByte()) {
                    true
                } else {
                    it.first == it.second
                }
            } == this.size
}
