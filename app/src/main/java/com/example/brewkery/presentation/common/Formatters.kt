package com.example.brewkery.presentation.common

import kotlin.math.abs

fun formatPrice(cents: Long, symbol: String): String {
    val sign = if (cents < 0) "-" else ""
    val absolute = abs(cents)
    return "$sign$symbol${absolute / 100}.${(absolute % 100).toString().padStart(2, '0')}"
}

/** The API says "20 - 30 mins"; the prototype's ticket says "20 - 30 minutes". */
fun formatWait(raw: String): String = raw.replace("mins", "minutes")