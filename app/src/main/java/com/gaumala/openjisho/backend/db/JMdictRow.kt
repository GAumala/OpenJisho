package com.gaumala.openjisho.backend.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jmdict")
data class JMdictRow(
    @PrimaryKey val id: Long,
    val entryJson: String)

