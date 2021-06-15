package com.gaumala.openjisho.backend.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tags",
    foreignKeys = [
        ForeignKey(
            entity = JMdictRow::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("tag"), Index("entryId")]
)
data class TagRow(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val tag: String,
    val entryId: Long)

