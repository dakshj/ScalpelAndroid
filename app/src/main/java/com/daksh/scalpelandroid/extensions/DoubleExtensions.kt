package com.daksh.scalpelandroid.extensions

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.round(decimals: Int = 2, roundingMode: RoundingMode = RoundingMode.HALF_UP) =
        BigDecimal(this).setScale(decimals, roundingMode).toDouble()
