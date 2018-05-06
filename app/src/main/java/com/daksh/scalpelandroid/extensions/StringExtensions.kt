package com.daksh.scalpelandroid.extensions

fun String.toScalpelBytes(): List<Byte> {
    val hexStartIndices = this.indices

            // Filter by backslash
            .filter { this[it] == '\\' }

            // This and next three chars should be within the length of the String
            .filter { it + 3 < this.length }

            // Confirm if the next char after the backslash is an 'x'
            .filter { this[it + 1] == 'x' }

    val result = mutableListOf<Byte>()

    var i = 0

    while (i < this.length) {
        i += if (i in hexStartIndices) {
            val hexString = this.substring(i + 2, i + 4)
            result.add(hexString.toInt(16).toByte())
            4
        } else {
            result.add(this[i].toByte())
            1
        }
    }

    return result
}
