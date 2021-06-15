package com.gaumala.openjisho.backend.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanjidic")
data class KanjidicRow(
    @PrimaryKey
    val literal: String,
    val strokes: Int,
    val entryJson: String)
