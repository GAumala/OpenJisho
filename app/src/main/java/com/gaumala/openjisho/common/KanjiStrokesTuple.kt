package com.gaumala.openjisho.common

import androidx.room.ColumnInfo

data class KanjiStrokesTuple(
    @ColumnInfo(name = "kanji") val kanji: String,
    @ColumnInfo(name = "strokes") val strokes: Int

)