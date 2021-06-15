package com.gaumala.openjisho.backend.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gaumala.openjisho.backend.setup.tatoeba.TatoebaSentence

@Entity(tableName = "jpn_sentences")
data class JpnSentenceRow(
    @PrimaryKey val id: Long,
    val japanese: String) {
    companion object {
        fun fromParsedSentence(parsedSentence: TatoebaSentence): JpnSentenceRow {
            return JpnSentenceRow(parsedSentence.id, parsedSentence.sentence)
        }
    }
}
