package com.gaumala.openjisho.utils

import java.lang.NumberFormatException


fun String.toNonNegativeLong(fallback: Long): Long = try {
    val parsed = this.toLong()
    if (parsed < 0)
        fallback
    else
        parsed
} catch (ex: NumberFormatException) {
    fallback
}

fun String.forEachCodepoint(callback: (Int, String) -> Unit) {
    val len = length
    var offset = 0

    while (offset < len) {
        val codepoint = codePointAt(offset)
        val codepointStr = String(intArrayOf(codepoint), 0, 1)
        callback(codepoint, codepointStr)

        offset += Character.charCount(codepoint)
    }
}