package com.gaumala.openjisho.frontend.sentence

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.db.JMdictRow
import com.gaumala.openjisho.backend.setup.jmdict.JMdictConverter
import com.gaumala.openjisho.backend.setup.tatoeba.TatoebaIndicesParser
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.WordIndex

class WordSearchEngine(private val dao: DictQueryDao) {
    private fun findMatchingEntryForIndex(
        rows: List<JMdictRow>, index: WordIndex
    ): SentenceWord {
        val headword = index.headword
        val usedForm = index.usedForm
        val reading = index.reading

        val matchesTargetWord: (JMdictEntry) -> Boolean =
            if (reading == null) { entry ->
                entry.kanjiElements.isEmpty() &&
                        entry.readingElements.any { it.text == headword }
            } else { entry ->
                entry.readingElements.any { it.text == reading }
            }

        val matchingEntry = rows
            .map { JMdictConverter.fromEntryRow(it) }
            .find(matchesTargetWord)
            ?: return createSentenceWordFromJMdictRow(rows.first())

        val summarized = JMdictEntry.Summarized.fromEntry(matchingEntry)
        return SentenceWord.JMdict(summarized)
    }

    private fun createSentenceWordFromJMdictRow(row: JMdictRow): SentenceWord {
        val entry = JMdictConverter.fromEntryRow(row)
        val summarized = JMdictEntry.Summarized.fromEntry(entry)
        return SentenceWord.JMdict(summarized)
    }

    private fun createUnknownSentenceWord(it: WordIndex): SentenceWord {
        return SentenceWord.Unknown(it.sentenceForm)
    }

    fun findSentenceWords(indices: String): List<SentenceWord> {
        return TatoebaIndicesParser.parseIndices(indices).map {
            val rows = dao.lookupJMdictRowsExact(it.displayForm)
            if (rows.size > 1)
                findMatchingEntryForIndex(rows, it)
            else if (rows.size == 1)
                createSentenceWordFromJMdictRow(rows.first())
            else
                createUnknownSentenceWord(it)
        }
    }
}