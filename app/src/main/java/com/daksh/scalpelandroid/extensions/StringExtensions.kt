package com.daksh.scalpelandroid.extensions

fun String.toScalpelBytes(): ByteArray {

    val hexStartIndices = this.indices

            // Filter by backslash
            .filter { this[it] == '\\' }

            // This and next three chars should be within the length of the String
            .filter { it + 3 < this.length }

            // Confirm if the next char after the backslash is an 'x'
            .filter { this[it + 1] == 'x' }

            .toList()

    val result = ByteArray(this.length - hexStartIndices.size * 3)

    var i = 0
    var resultI = 0

    while (i < this.length) {
        if (i in hexStartIndices) {
            val hexString = this.substring(i + 2, i + 4)
            result[resultI] = hexString.toInt(16).toByte()
            i += 4
        } else {
            result[resultI] = this[i].toByte()
            i += 1
        }

        resultI++
    }

    return result
}
