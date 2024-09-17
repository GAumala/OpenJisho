package com.gaumala.openjisho.frontend.sentence

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.db.JMdictRow
import com.gaumala.openjisho.backend.setup.jmdict.JMdictConverter
import com.gaumala.openjisho.backend.setup.tatoeba.TatoebaIndicesParser
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.WordIndex

class WordSearchEngine(private val dao: DictQueryDao) {
    private val cache = HashMap<String, SentenceWord>()

    private fun SentenceWord.saveInCache(key: String) {
        cache[key] = this
        // should we evict older cache entries? idk
    }

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
            ?: return createSentenceWordFromJMdictRow(usedForm, rows.first())

        val summarized = JMdictEntry.Summarized.fromEntry(matchingEntry)
        return SentenceWord.JMdict(usedForm, summarized)
    }

    private fun createSentenceWordFromJMdictRow(usedForm: String?, row: JMdictRow): SentenceWord {
        val entry = JMdictConverter.fromEntryRow(row)
        val summarized = JMdictEntry.Summarized.fromEntry(entry)
        return SentenceWord.JMdict(usedForm, summarized)
    }

    private fun createUnknownSentenceWord(it: WordIndex): SentenceWord {
        return SentenceWord.Unknown(it.sentenceForm)
    }

    private fun lookupSentenceWord(wordIndex: WordIndex): SentenceWord {
        val rows = dao.lookupJMdictRowsExact(wordIndex.displayForm)
        return if (rows.size > 1)
            findMatchingEntryForIndex(rows, wordIndex)
        else if (rows.size == 1)
            createSentenceWordFromJMdictRow(wordIndex.usedForm, rows.first())
        else
            createUnknownSentenceWord(wordIndex)
    }

    fun findSentenceWords(indices: String) =
        findSentenceWords(TatoebaIndicesParser.parseIndices(indices))

    private fun findSentenceWordInCache(wordIndex: WordIndex): SentenceWord? {
        val cached = cache[wordIndex.displayForm] ?: return null
        if (cached is SentenceWord.JMdict && cached.usedForm != wordIndex.usedForm) {
            // entries in the cache may have a different usedForm, we have to fix that
            return cached.copy(usedForm = wordIndex.usedForm)
        }
        return cached
    }

    fun findSentenceWords(indices: List<WordIndex>): List<SentenceWord> = indices.map { wordIndex ->
        findSentenceWordInCache(wordIndex)
            ?: lookupSentenceWord(wordIndex).apply { saveInCache(wordIndex.displayForm) }
    }
}