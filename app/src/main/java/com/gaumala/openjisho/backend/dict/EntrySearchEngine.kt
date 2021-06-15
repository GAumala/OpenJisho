package com.gaumala.openjisho.backend.dict

import com.gaumala.openjisho.backend.JMdictQuery
import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.db.JMdictRow
import com.gaumala.openjisho.backend.db.KanjidicRow
import com.gaumala.openjisho.backend.setup.jmdict.JMdictConverter
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicConverter
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.frontend.dict.EntryResult

/**
 * A class that can search for entries in the database using pagination.
 */
class EntrySearchEngine(private val dao: DictQueryDao,
                        private val pageSize: Int) {

    private val jmdictRowToEntryResult: (JMdictRow) -> EntryResult = { entryRow ->
        val entry = JMdictConverter.fromEntryRow(entryRow)
        EntryResult.JMdict(JMdictEntry.Summarized.fromEntry(entry))
    }

    private val kanjidicRowTowEntryResult: (KanjidicRow) -> EntryResult = { kanjiRow ->
        val entry = KanjidicConverter.fromKanjiRow(kanjiRow)
        EntryResult.Kanjidic(entry)
    }

    private fun mergeEntryResults(
        jmDictEntries: List<EntryResult>,
        kanjidicEntries: List<EntryResult>): List<EntryResult> {

        if (kanjidicEntries.isEmpty())
            return jmDictEntries

        if (jmDictEntries.isEmpty())
            return kanjidicEntries

        val result = ArrayList(jmDictEntries)
        if (jmDictEntries.size >= 3)
            result.addAll(3, kanjidicEntries)
        else
            result.addAll(kanjidicEntries)
        return result
    }

    fun search(queryText: String, offset: Int): List<EntryResult> {
        val query = JMdictQuery.resolve(queryText) ?: throw IllegalArgumentException()

        val jmDictEntries = dao.lookupEntries(query, pageSize, offset)
            .map(jmdictRowToEntryResult)
        if (offset > 0)
            return jmDictEntries

        // First page
        val kanjidicQuery = queryText.toCharArray()
            .map { it.toString() }

        val kanjidicEntries = dao.lookupKanjidicRowExact(kanjidicQuery)
            .map(kanjidicRowTowEntryResult)

        return mergeEntryResults(jmDictEntries, kanjidicEntries)
    }
}