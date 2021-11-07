package com.gaumala.openjisho.backend.dict

import com.gaumala.openjisho.backend.JMdictQuery
import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.db.JMdictRow
import com.gaumala.openjisho.backend.db.KanjidicRow
import com.gaumala.openjisho.backend.setup.jmdict.JMdictConverter
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicConverter
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.frontend.QuerySuggestion
import com.gaumala.openjisho.frontend.dict.EntryResult
import com.gaumala.openjisho.utils.error.BetterQueriesException

/**
 * A class that can search for entries in the database using pagination.
 */
class EntrySearchEngine(private val dao: DictQueryDao,
                        private val pageSize: Int) {

    private fun jmdictRowToEntryResult(
        targetHeader: String
    ): (JMdictRow) -> EntryResult.JMdict = { entryRow ->
        val entry = JMdictConverter.fromEntryRow(entryRow)
        val summarized = JMdictEntry.Summarized.fromEntry(entry, targetHeader)
        EntryResult.JMdict(summarized)
    }

    private val kanjidicRowTowEntryResult: (KanjidicRow) -> EntryResult = { kanjiRow ->
        val entry = KanjidicConverter.fromKanjiRow(kanjiRow)
        EntryResult.Kanjidic(entry)
    }

    private fun mergeEntryResults(
        query: JMdictQuery,
        jmDictEntries: List<EntryResult>,
        kanjidicEntries: List<EntryResult>
    ): List<EntryResult> {
        val result = ArrayList<EntryResult>()

        if (query is JMdictQuery.Exact) {
            // for exact queries first add Jmdict results
            result.addAll(jmDictEntries)
            // then we add all the kanji results
            result.addAll(kanjidicEntries)
        } else {
            // if query not exact, don't add any kanji
            result.addAll(jmDictEntries)
        }

        return result
    }

    private fun createSuggestion(
        newQuery: JMdictQuery.Like,
        newResults: List<JMdictRow>
    ) = QuerySuggestion(
        queryText = newQuery.likeText,
        results = newResults.map {
            val entry = JMdictConverter.fromEntryRow(it)
            val summary = JMdictEntry.Summarized.fromEntry(
                entry, newQuery.likeText
            )
            EntryResult.JMdict(summary)
        }
    )

    private fun getQueriesToSuggest(
        query: JMdictQuery,
        exactQueryResults: List<JMdictRow>,
        pageSize: Int
    ): List<QuerySuggestion> {
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
        return ArrayList<QuerySuggestion>().apply {
            if (shouldAppendUnderscore)
                add(createSuggestion(underscoreQuery, underscoreResults))
            if (shouldAppendPercent)
                add(createSuggestion(percentQuery, percentResults))
        }
    }

    fun search(queryText: String, offset: Int): List<EntryResult> {
        val query = JMdictQuery.resolve(queryText)
            ?: throw IllegalArgumentException()

        val jmDictRows = dao.lookupEntries(query, pageSize, offset)
        val jmDictEntries = jmDictRows.map(jmdictRowToEntryResult(queryText))
        if (offset > 0)
            return jmDictEntries

        // First page
        val kanjidicQuery = queryText.toCharArray()
            .map { it.toString() }

        // first page should include kanjidic entries
        val kanjidicEntries = dao.lookupKanjidicRowExact(kanjidicQuery)
            .map(kanjidicRowTowEntryResult)

        if (jmDictEntries.isEmpty() && kanjidicEntries.isEmpty()) {
            // If there are no results for the current query,
            // try to come up with some suggestions in the error message
            val suggestions = getQueriesToSuggest(query, jmDictRows, pageSize)
            if (suggestions.isNotEmpty())
                throw BetterQueriesException(suggestions)
        }

        return mergeEntryResults(query, jmDictEntries, kanjidicEntries)
    }

    fun getSuggestionsForLastEntryResults(
        queryText: String,
        results: List<EntryResult.JMdict>
    ): List<QuerySuggestion> {
        val query = JMdictQuery.resolve(queryText)
            ?: return emptyList() // this should never happen

        val jmDictRows = results.map {
            val rowsHolder = JMdictConverter.toDBRows(it.summary.entry)
            rowsHolder.jmDictRow
        }
        return getQueriesToSuggest(query, jmDictRows, pageSize)
    }
}