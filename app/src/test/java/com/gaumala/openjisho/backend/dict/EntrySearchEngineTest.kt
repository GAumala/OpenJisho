package com.gaumala.openjisho.backend.dict

import com.gaumala.openjisho.backend.JMdictQuery
import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.setup.jmdict.JMdictConverter
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicConverter
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
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
     * Tests for the first page of results which besides jmdict entries can also include
     * kanjidic rows and suggestions
     */
    @Test
    fun `if exact query has results, should not include suggestions if these don't have different results`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return 1 jmdict row with exact query
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("入"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow1)
        // mock dao to return 0 rows for all suggestions
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入_"), PAGE_SIZE, 0)
        } returns emptyList()
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入%"), PAGE_SIZE, 0)
        } returns emptyList()
        // mock dao to return 1 kanjidic row with expected kanji
        every {
            dictQueryDao.lookupKanjidicRowExact(listOf("入"))
        } returns listOf(sampleKanjidicRow1)

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val searchResults = engine.search("入", 0)

        // the result should only have the Jmdict and Kanjidic rows
        searchResults `should equal` listOf(
            EntryResult.JMdict(JMdictEntry.Summarized.fromEntry(sampleJmdictEntry1)),
            EntryResult.Kanjidic(sampleKanjidicEntry1)
        )
    }

    @Test
    fun `if exact query has results, and only the percent suggestion has more results, should only include that suggestion`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return 1 jmdict row with exact query
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("入"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow1)
        // mock dao to return 0 rows for underscore suggestion
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入_"), PAGE_SIZE, 0)
        } returns emptyList()
        // mock dao to return more rows for percent suggestion
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入%"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow2)
        // mock dao to return 1 kanjidic row with expected kanji
        every {
            dictQueryDao.lookupKanjidicRowExact(listOf("入"))
        } returns listOf(sampleKanjidicRow1)

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val searchResults = engine.search("入", 0)

        // the result should only have the Jmdict and Kanjidic rows
        // AND only one suggestion
        searchResults `should equal` listOf(
            EntryResult.JMdict(JMdictEntry.Summarized.fromEntry(sampleJmdictEntry1)),
            EntryResult.Kanjidic(sampleKanjidicEntry1),
            EntryResult.Suggestion(originalQuery = "入", suggestedQueries = listOf("入%"))
        )
    }

    @Test
    fun `if exact query has results, and both suggestion have more results, should include both suggestion`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return 1 jmdict row with exact query
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("入"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow1)
    // mock dao to return more rows for underscore suggestion
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入_"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow2)
        // mock dao to return more rows for percent suggestion
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入%"), PAGE_SIZE, 0)
        } returns listOf(sampleJmdictRow2)
    // mock dao to return 1 kanjidic row with expected kanji
        every {
            dictQueryDao.lookupKanjidicRowExact(listOf("入"))
        } returns listOf(sampleKanjidicRow1)

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val searchResults = engine.search("入", 0)

        // the result should have the Jmdict and Kanjidic rows
        // AND both suggestions
        searchResults `should equal` listOf(
            EntryResult.JMdict(JMdictEntry.Summarized.fromEntry(sampleJmdictEntry1)),
            EntryResult.Kanjidic(sampleKanjidicEntry1),
            EntryResult.Suggestion(
                originalQuery = "入",
                suggestedQueries = listOf("入_", "入%")
            )
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
     * Other scenarios
     */
    @Test
    fun `should not try to get suggestions or kanji rows if we are not fetching the 1st page`() {
        val dictQueryDao = mockk<DictQueryDao>()
        // mock dao to return 0 rows with exact query
        every {
            dictQueryDao.lookupEntries(JMdictQuery.Exact("入"), PAGE_SIZE, 50)
        } returns emptyList()

        val engine = EntrySearchEngine(dictQueryDao, PAGE_SIZE)
        val searchResult = engine.search("入", 50)

        searchResult.`should be empty`()
        // verify that no queries related to kanji or suggestions were made
        verify(inverse = true) {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入_"), any(), any())
        }
        verify(inverse = true) {
            dictQueryDao.lookupEntries(JMdictQuery.Like("入%"), any(), any())
        }
        verify(inverse = true) {
            dictQueryDao.lookupKanjidicRowExact(any<List<String>>())
        }
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
                JMdictEntry.Element("入学 ", tags = emptyList())
            ),
            readingElements = listOf(
                JMdictEntry.Element(" にゅうがく", tags = emptyList())
            ),
            senseElements = listOf(
                JMdictEntry.Sense(listOf("Matriculation"), emptyList())
            )
        )
        val sampleJmdictRow1 = JMdictConverter.toDBRows(sampleJmdictEntry1).jmDictRow
        val sampleJmdictRow2 = JMdictConverter.toDBRows(sampleJmdictEntry2).jmDictRow
        val sampleKanjidicEntry1 = KanjidicEntry(
            literal = "入",
            grade = 1,
            jlpt = 5,
            strokeCount = 2,
            meanings = listOf("Enter"),
            onReadings = listOf("いり"),
            kunReadings = listOf("いり")
        )
        val sampleKanjidicRow1 = KanjidicConverter.toDBRow(sampleKanjidicEntry1)
    }
}