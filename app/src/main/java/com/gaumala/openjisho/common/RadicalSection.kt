package com.gaumala.openjisho.common

import com.gaumala.openjisho.backend.db.RadicalRow
import java.util.*

data class RadicalSection(val radical: String,
                          val strokes: Int,
                          val kanji: LinkedList<String>) {
    constructor(radical: String, strokes: Int)
            : this (radical, strokes, LinkedList())

    fun toRows(): List<RadicalRow> =
        kanji.map { RadicalRow(radical, it) }
}
