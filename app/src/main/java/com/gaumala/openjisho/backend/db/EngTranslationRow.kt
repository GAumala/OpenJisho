package com.gaumala.openjisho.backend.db

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "eng_translations")
@Fts4
data class EngTranslationRow(
    @PrimaryKey(autoGenerate = true)
    val rowid: Long,
    val japaneseId: Long,
    val english: String)
