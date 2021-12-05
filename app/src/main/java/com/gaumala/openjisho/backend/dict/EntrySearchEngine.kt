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

    private val kanjidicRowToEntryResult: (KanjidicRow) -> EntryResult = { kanjiRow ->
        val entry = KanjidicConverter.fromKanjiRow(kanjiRow)
        EntryResult.Kanjidic(entry)
    }

    private fun EntryResult.JMdict.hasExactKanjiOrReading(target: String): Boolean {
        val matchingKanji = summary.entry.kanjiElements.find {
            it.text == target
        }
        val matchingReading = summary.entry.readingElements.find {
            it.text == target
        }
        return matchingKanji != null || matchingReading != null
    }

    private fun EntryResult.JMdict.hasExactSense(target: String): Boolean {
        val matchingSense = summary.entry.senseElements.find { senseItem ->
            val matchingGloss = senseItem.glossItems.find {
                it.toLowerCase() == target.toLowerCase()
            }
            matchingGloss != null
        }
        return matchingSense != null
    }

    private fun mergeEntryResults(
        query: JMdictQuery,
        jmDictEntries: List<EntryResult.JMdict>,
        kanjidicEntries: List<EntryResult>
    ): List<EntryResult> {
        val result = ArrayList<EntryResult>(kanjidicEntries)
        when (query) {
            is JMdictQuery.Exact -> {
                // for exact queries we place jmdict entries that have
                // the exact query text in a kanji or reading
                // element at the top.
                // The rest are placed at the bottom.
                jmDictEntries.forEach { jmdictEntry ->
                    if (jmdictEntry.hasExactKanjiOrReading(query.queryText)) {
                        result.add(0, jmdictEntry)
                    } else result.add(jmdictEntry)
                }
            }
            is JMdictQuery.EnglishMatch -> {
                // for queries in English we place jmdict entries that have
                // the exact query text in sense element at the top.
                // The rest are placed at the bottom.
                jmDictEntries.forEach { jmdictEntry ->
                    if (jmdictEntry.hasExactSense(query.englishText)) {
                        result.add(0, jmdictEntry)
                    } else result.add(jmdictEntry)
                }
            }
            else -> {
                // Other query types don't need special sorting
                result.addAll(jmDictEntries)
            }
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

    private fun getKanjidicResults(query: JMdictQuery): List<EntryResult> {
        // We only show kanji for exact queries
        if (query !is JMdictQuery.Exact) return emptyList()

        val kanjiList = query.queryText
            .toCharArray()
            .map { it.toString() }

        // first page should include kanjidic entries
        return dao.lookupKanjidicRowExact(kanjiList)
            .sortedWith(KanjidicComparator(query.queryText))
            .map(kanjidicRowToEntryResult)
    }

    fun search(queryText: String, offset: Int): List<EntryResult> {
        val query = JMdictQuery.resolve(queryText)
            ?: throw IllegalArgumentException()

        val jmDictRows = dao.lookupEntries(query, pageSize, offset)
        val jmDictEntries = jmDictRows.map(jmdictRowToEntryResult(queryText))
        if (offset > 0) // return the nth page
            return mergeEntryResults(query, jmDictEntries, emptyList())

        // Create the first page

        // first page should include kanjidic results
        val kanjidicEntries = getKanjidicResults(query)
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