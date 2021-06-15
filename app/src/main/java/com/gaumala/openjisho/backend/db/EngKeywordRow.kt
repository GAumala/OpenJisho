package com.gaumala.openjisho.backend.db

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "eng_keywords")
@Fts4
data class EngKeywordRow(
    @PrimaryKey(autoGenerate = true)
    val rowid: Long,
    val entryId: Long,
    val keywords: String)