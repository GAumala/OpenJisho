package com.gaumala.openjisho.backend.db

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "jpn_indices")
@Fts4
data class JpnIndicesRow(
    @PrimaryKey(autoGenerate = true)
    val rowid: Long,
    val japaneseId: Long,
    val indices: String)
