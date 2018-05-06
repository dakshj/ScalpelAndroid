package com.daksh.scalpelandroid.extensions

fun Byte.toUnsignedInt(): Int = this.toInt() and 0xff
