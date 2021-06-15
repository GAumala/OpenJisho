package com.gaumala.openjisho.backend.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "jpn_keywords",
    foreignKeys = [
        ForeignKey(
            entity = JMdictRow::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("keyword"), Index("entryId")]
)
data class JpnKeywordRow(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val keyword: String,
    val entryId: Long)