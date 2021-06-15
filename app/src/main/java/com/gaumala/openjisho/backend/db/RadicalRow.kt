package com.gaumala.openjisho.backend.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "radicals",
        indices = [Index("radical", "kanji")])
data class RadicalRow(
    @PrimaryKey(autoGenerate = true)
    val rowid: Long,
    val radical: String,
    val kanji: String) {

    constructor(radical: String, kanji: String)
            : this(0, radical, kanji)
}