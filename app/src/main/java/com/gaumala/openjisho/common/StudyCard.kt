package com.gaumala.openjisho.common

sealed class StudyCard {

    abstract fun getCardId(): Long

    data class JMdict(val id: Long,
                      val summarized: JMdictEntry.Summarized): StudyCard() {

        constructor(id: Long, entry: JMdictEntry)
                : this(id, JMdictEntry.Summarized.fromEntry(entry))

        val entry: JMdictEntry
            get() = summarized.entry

        override fun getCardId() = id
    }

    data class Kanjidic(val id: Long,
                        val entry: KanjidicEntry): StudyCard() {

        override fun getCardId() = id
    }

    data class Text(val id: Long, val text: String): StudyCard() {

        override fun getCardId() = id
    }

    data class Sentence(val id: Long,
                        val japanese: String,
                        val english: String): StudyCard() {

        override fun getCardId() = id
    }

    data class NotFound(val id: Long,
                        val source: String,
                        val hint: String): StudyCard() {

        override fun getCardId() = id
    }
}