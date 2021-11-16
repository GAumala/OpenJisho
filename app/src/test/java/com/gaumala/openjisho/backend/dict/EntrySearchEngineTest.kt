package com.gaumala.openjisho.backend.dict

import com.gaumala.openjisho.backend.JMdictQuery
import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.setup.jmdict.JMdictConverter
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicConverter
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.frontend.QuerySuggestion
import com.gaumala.openjisho.frontend.dict.EntryResult
import com.gaumala.openjisho.utils.error.BetterQueriesException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.Test

class EntrySearchEngineTest {

    /*
     * Tests for search() method, specifically,  for the first page of
     * results, which besides jmdict entries can also include
     * kanjidic rows and suggestions
     */
    @Test
    fun `if exact query has results, return those without trying to load suggestions`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return appropriate rows with exact query
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("入"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow1)
        // mock dao to return appropriate kanjidic rows
        every {
            dictQueryDao.lookupKanjidicRowExact(listOf("入"))
        } returns listOf(sampleKanjidicRow1)

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val results = engine.search("入", 0)

        results `should equal` listOf(
            EntryResult.JMdict(sampleJmdictSummary1),
            EntryResult.Kanjidic(sampleKanjidicEntry1)
        )

        // verify that no queries related to kanji were made
        verify(inverse = true) {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入_"), any(), any())
        }
        verify(inverse = true) {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入%"), any(), any())
        }
    }

    @Test
    fun `if exact query has results, it should sort results correctly`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return appropriate rows with exact query
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("入学"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow2)
        // mock dao to return appropriate kanjidic rows without sorting
        every {
            dictQueryDao.lookupKanjidicRowExact(listOf("入", "学"))
        } returns listOf(sampleKanjidicRow2, sampleKanjidicRow1)

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val results = engine.search("入学", 0)

        // assert that the jmdict entry is first because the header
        // matches the query text and that the following kanji are
        // in the same order in which they appear in the query text.
        results `should equal` listOf(
            EntryResult.JMdict(sampleJmdictSummary2),
            EntryResult.Kanjidic(sampleKanjidicEntry1),
            EntryResult.Kanjidic(sampleKanjidicEntry2)
        )
    }

    @Test
    fun `if english query has results, it should sort results correctly`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return some rows without sorting
        every {
            dictQueryDao.lookupEntries(
                JMdictQuery.EnglishMatch("matriculation"),
                PAGE_SIZE,
                0
            )
        } returns listOf(sampleJmdictRow1, sampleJmdictRow2)

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val results = engine.search("matriculation", 0)

        // assert that the jmdict entry for "入学" is first because
        // the only sense item (Matriculation) matches the query text
        results `should equal` listOf(
            EntryResult.JMdict(sampleJmdictSummary2),
            EntryResult.JMdict(sampleJmdictSummary1)
        )
    }

    @Test
    fun `if exact query has NO results, but both suggestion have more results, should throw custom exception`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return 0 rows with exact query
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("入"), PAGE_SIZE, 0)
        } returns emptyList()
        // mock dao to return more rows for underscore suggestion
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入_"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow2)
        // mock dao to return more rows for percent suggestion
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入%"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow2)
        // mock dao to return 0 kanjidic rows
        every {
            dictQueryDao.lookupKanjidicRowExact(listOf("入"))
        } returns emptyList()

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)

        var didThrow = false
        try {
            engine.search("入", 0)
        } catch (ex: BetterQueriesException) {
            didThrow = true
        }

        didThrow `should be` true
    }

    /*
     * Other search() scenarios
     */
    @Test
    fun `should not try to get kanji rows if we are not fetching the 1st page`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return 0 rows with exact query for 2nd page
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("入"), PAGE_SIZE, 50)
        } returns emptyList()

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val searchResult = engine.search("入", 50)

        searchResult.`should be empty`()
        // verify that no queries related to kanji were made
        verify(inverse = true) {
            dictQueryDao.lookupKanjidicRowExact(any<List<String>>())
        }
    }
    @Test
    fun `the summarized entry should use a header that matches the query text or default to the first kanji`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return the appropriate entry for our query with hiragana
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("かなう"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow3)
        // mock dao to return the same entry with a query using an alternative kanji
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("敵う"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow3)
        // mock dao to not return any kanji rows
        every {
            dictQueryDao.lookupKanjidicRowExact(any<List<String>>())
        } returns emptyList()

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val searchResultWithHiragana = engine.search("かなう", 0)
        val searchResultWithKanji = engine.search("敵う", 0)

        searchResultWithHiragana `should equal` listOf(
            EntryResult.JMdict(sampleJmdictSummary3)
        )
        searchResultWithKanji `should equal` listOf(
            EntryResult.JMdict(
                // different query text, different header
                sampleJmdictSummary3.copy(header = "敵う")
            )
        )
    }

    /*
     * Tests for get getSuggestionsForLastEntryResults()
     */
    @Test
    fun `should return empty list if the last query was not exact`() {
        val dictQueryDao = mockk<DictQueryDao>()
        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)

        val lastResults = listOf(
            EntryResult.JMdict(sampleJmdictSummary1)
        )
        // wildcard query means it is not exact
        val suggestions = engine.getSuggestionsForLastEntryResults("入%", lastResults)
        suggestions.`should be empty`()
    }

    @Test
    fun `should return empty list if the last query had more than one page of results`() {
        val dictQueryDao = mockk<DictQueryDao>()
        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)

        // construct the last results parameter as a list
        // with a whole page worth of items
        val lastResults = ArrayList<EntryResult.JMdict>()
        for (i in 1..PAGE_SIZE) {
            lastResults.add(EntryResult.JMdict(sampleJmdictSummary1))
        }

        val suggestions = engine.getSuggestionsForLastEntryResults("入", lastResults)
        suggestions.`should be empty`()
    }

    @Test
    fun `should return the rows returned by the dao as QuerySuggestion objects`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return more rows for underscore suggestion
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入_"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow2)
        // mock dao to return more rows for percent suggestion
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入%"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow1, sampleJmdictRow2)


        val lastResults = listOf(
            EntryResult.JMdict(
                JMdictEntry.Summarized.fromEntry(sampleJmdictEntry1)
            )
        )

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val suggestions =
            engine.getSuggestionsForLastEntryResults("入", lastResults)
        suggestions `should equal` listOf(
            QuerySuggestion(
                "入_",
                listOf(EntryResult.JMdict(sampleJmdictSummary2))
            ),
            QuerySuggestion(
                "入%",
                listOf(
                    EntryResult.JMdict(sampleJmdictSummary1),
                    EntryResult.JMdict(sampleJmdictSummary2)
                )
            )
        )
    }

    private companion object {
        const val PAGE_SIZE = 40
        val sampleJmdictEntry1 = JMdictEntry(
            entryId = 1L,
            kanjiElements = listOf(
                JMdictEntry.Element("入", tags = emptyList())
            ),
            readingElements = listOf(
                JMdictEntry.Element("いり", tags = emptyList())
            ),
            senseElements = listOf(
                JMdictEntry.Sense(listOf("Enter"), emptyList())
            )
        )
        val sampleJmdictEntry2 = JMdictEntry(
            entryId = 1L,
            kanjiElements = listOf(
                JMdictEntry.Element("入学", tags = emptyList())
            ),
            readingElements = listOf(
                JMdictEntry.Element(" にゅうがく", tags = emptyList())
            ),
            senseElements = listOf(
                JMdictEntry.Sense(listOf("Matriculation"), emptyList())
            )
        )
        val sampleJmdictEntry3 = JMdictEntry(
            entryId = 1L,
            kanjiElements = listOf(
                JMdictEntry.Element("叶う", tags = emptyList()),
                JMdictEntry.Element("敵う", tags = emptyList())
            ),
            readingElements = listOf(
                JMdictEntry.Element("かなう", tags = emptyList())
            ),
            senseElements = listOf(
                JMdictEntry.Sense(listOf("To come true"), emptyList())
            )
        )
        val sampleJmdictRow1 = JMdictConverter.toDBRows(sampleJmdictEntry1).jmDictRow
        val sampleJmdictRow2 = JMdictConverter.toDBRows(sampleJmdictEntry2).jmDictRow
        val sampleJmdictRow3 = JMdictConverter.toDBRows(sampleJmdictEntry3).jmDictRow
        val sampleJmdictSummary1 = JMdictEntry.Summarized.fromEntry(sampleJmdictEntry1)
        val sampleJmdictSummary2 = JMdictEntry.Summarized.fromEntry(sampleJmdictEntry2)
        val sampleJmdictSummary3 = JMdictEntry.Summarized.fromEntry(sampleJmdictEntry3)
        val sampleKanjidicEntry1 = KanjidicEntry(
            literal = "入",
            grade = 1,
            jlpt = 5,
            strokeCount = 2,
            meanings = listOf("Enter"),
            onReadings = listOf("いり"),
            kunReadings = listOf("いり")
        )
        val sampleKanjidicEntry2 = KanjidicEntry(
            literal = "学",
            grade = 1,
            jlpt = 5,
            strokeCount = 8,
            meanings = listOf("Education"),
            onReadings = listOf("がく"),
            kunReadings = listOf("がく")
        )
        val sampleKanjidicRow1 = KanjidicConverter.toDBRow(sampleKanjidicEntry1)
        val sampleKanjidicRow2 = KanjidicConverter.toDBRow(sampleKanjidicEntry2)
    }
}