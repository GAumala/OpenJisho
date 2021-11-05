package com.gaumala.openjisho.backend.dict

import com.gaumala.openjisho.backend.JMdictQuery
import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.db.JMdictRow
import com.gaumala.openjisho.backend.db.KanjidicRow
import com.gaumala.openjisho.backend.setup.jmdict.JMdictConverter
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicConverter
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.frontend.dict.EntryResult
import com.gaumala.openjisho.utils.error.BetterQueriesException

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
        kanjidicEntries: List<EntryResult>,
        suggestion: EntryResult.Suggestion?
    ): List<EntryResult> {

        if (kanjidicEntries.isEmpty())
            return jmDictEntries

        if (jmDictEntries.isEmpty())
            return kanjidicEntries

        val result = ArrayList(jmDictEntries)
        if (jmDictEntries.size >= 3)
            result.addAll(3, kanjidicEntries)
        else
            result.addAll(kanjidicEntries)

        if (suggestion != null)
            result.add(suggestion)

        return result
    }

    private fun getQueriesToSuggest(
        query: JMdictQuery,
        exactQueryResults: List<JMdictRow>,
        pageSize: Int
    ): List<String> {
        // We should only suggest appending wild cards
        // If there's only one page of results and the user
        // isn't already using them.
        if (exactQueryResults.size >= pageSize) return emptyList()
        val originalQuery = if (query is JMdictQuery.Exact) query.queryText
                            else return emptyList()

        val underscoreQuery = JMdictQuery.Like(originalQuery + '_')
        val percentQuery = JMdictQuery.Like(originalQuery + '%')
        val underscoreResults = dao.lookupEntries(
            underscoreQuery,
            pageSize,
            0
        )
        val percentResults = dao.lookupEntries(
            percentQuery,
            pageSize,
            0
        )
        val shouldAppendUnderscore = underscoreResults.isNotEmpty()
                && underscoreResults != exactQueryResults
        val shouldAppendPercent = percentResults.isNotEmpty()
                && percentResults != exactQueryResults
        // Only include suggestions if they actually net new results
        return ArrayList<String>().apply {
            if (shouldAppendUnderscore) add(underscoreQuery.likeText)
            if (shouldAppendPercent) add(percentQuery.likeText)
        }
    }

    fun search(queryText: String, offset: Int): List<EntryResult> {
        val query = JMdictQuery.resolve(queryText)
            ?: throw IllegalArgumentException()

        val jmDictRows = dao.lookupEntries(query, pageSize, offset)
        val jmDictEntries = jmDictRows.map(jmdictRowToEntryResult)
        if (offset > 0)
            return jmDictEntries

        // First page
        val kanjidicQuery = queryText.toCharArray()
            .map { it.toString() }

        // first page should include kanjidic entries
        val kanjidicEntries = dao.lookupKanjidicRowExact(kanjidicQuery)
            .map(kanjidicRowTowEntryResult)

        // try to append query suggestions
        val suggestions = getQueriesToSuggest(query, jmDictRows, pageSize)
        if (suggestions.isNotEmpty()) {
            // if the current query has no results, but there are better
            // queries in the suggestions, show this information as an
            // error message
            if (jmDictEntries.isEmpty()) throw BetterQueriesException(suggestions)
            val suggestionItem = EntryResult.Suggestion(queryText, suggestions)
            return mergeEntryResults(
                jmDictEntries,
                kanjidicEntries,
                suggestionItem
            )
        }

        return mergeEntryResults(jmDictEntries, kanjidicEntries, null)
    }
}