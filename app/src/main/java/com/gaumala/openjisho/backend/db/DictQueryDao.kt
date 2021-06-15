package com.gaumala.openjisho.backend.db

import androidx.room.Dao
import androidx.room.Query
import com.gaumala.openjisho.backend.JMdictQuery
import com.gaumala.openjisho.backend.TatoebaQuery
import com.gaumala.openjisho.common.KanjiStrokesTuple
import com.gaumala.openjisho.common.Sentence

@Dao
abstract class DictQueryDao {
    companion object {
        const val allJMdictRows = "jmdict.id, jmdict.entryJson"
        const val allKanjidicRows = "kanjidic.literal, kanjidic.strokes, kanjidic.entryJson"
    }

    @Query("""
        SELECT $allJMdictRows FROM jmdict
        WHERE :id = id LIMIT 1
    """)
    abstract fun lookupJMdictRowById(id: Long): JMdictRow?

    @Query("""
        SELECT $allJMdictRows FROM jmdict
        JOIN jpn_keywords ON jmdict.id = jpn_keywords.entryId
        WHERE :queryText = keyword
    """)
    abstract fun lookupJMdictRowsExact(queryText: String): List<JMdictRow>

    @Query("""
        SELECT $allJMdictRows FROM jmdict
        JOIN jpn_keywords ON jmdict.id = jpn_keywords.entryId
        WHERE keyword LIKE :queryText LIMIT :limit OFFSET :offset
    """)
    abstract fun lookupJMdictRowsLike(queryText: String, limit: Int, offset: Int): List<JMdictRow>

    @Query("""
        SELECT $allJMdictRows FROM jmdict
        JOIN eng_keywords ON jmdict.id = eng_keywords.entryId
        WHERE keywords MATCH :queryText LIMIT :limit OFFSET :offset
    """)
    abstract fun lookupJMdictRowsEnglishMatch(queryText: String, limit: Int, offset: Int): List<JMdictRow>

    @Query("""
        SELECT $allKanjidicRows FROM kanjidic
        WHERE :queryText = literal LIMIT 1
    """)
    abstract fun lookupKanjidicRowExact(queryText: String): KanjidicRow?

    @Query("""
        SELECT $allKanjidicRows FROM kanjidic
        WHERE literal IN (:kanjiList)
    """)
    abstract fun lookupKanjidicRowExact(kanjiList: List<String>): List<KanjidicRow>

    @Query("""
        SELECT kanji, strokes FROM radicals
        JOIN kanjidic ON kanjidic.literal = radicals.kanji
        WHERE radical IN (:radicals)
        GROUP BY kanji
        HAVING count(*) == :totalRadicals
        ORDER BY strokes
    """)
    abstract fun lookupKanjiByRadicals(radicals: List<String>, totalRadicals: Int): List<KanjiStrokesTuple>

    @Query("""
        SELECT kanji  FROM radicals
        WHERE radical IN (:radicals)
    """)
    abstract fun lookupKanjiByRadicals(radicals: List<String>): List<String>
    @Query("""
        SELECT count(*) FROM (
            SELECT kanji  AS occurrences FROM radicals
            WHERE radical IN (:radicals)
            GROUP BY kanji
            HAVING count(*) == :totalRadicals
        )
    """)
    abstract fun countKanjiByRadicals(radicals: List<String>, totalRadicals: Int): Int

    @Query("""
        SELECT * FROM jpn_sentences
        WHERE japanese LIKE :queryText LIMIT :limit OFFSET :offset
    """)
    abstract fun lookupSentencesLike(queryText: String, limit: Int, offset: Int): List<JpnSentenceRow>
    @Query("""
        SELECT japaneseId FROM jpn_indices
        WHERE indices MATCH :queryText LIMIT :limit OFFSET :offset
    """)
    abstract fun lookupSentenceIdsMatchingIndices(queryText: String, limit: Int, offset: Int): List<Long>

    @Query("""
        SELECT * FROM jpn_sentences
        WHERE id IN (:ids)
    """)
    abstract fun lookupSentencesById(ids: List<Long>): List<JpnSentenceRow>

    @Query("""
        SELECT `rowid`, japaneseId, english FROM eng_translations
        WHERE english MATCH :queryText LIMIT :limit OFFSET :offset
    """)
    abstract fun lookupTranslationsMatch(queryText: String, limit: Int, offset: Int): List<EngTranslationRow>

    @Query("""
        SELECT `rowid`, japaneseId, english FROM eng_translations
        WHERE japaneseId IN (:ids)
    """)
    abstract fun lookupTranslationsById(ids: List<Long>): List<EngTranslationRow>

    @Query("""
        SELECT `rowid`, japaneseId, indices FROM jpn_indices
        WHERE japaneseId = :sentenceId
    """)
    abstract fun lookupSentenceWordIndices(sentenceId: Long): JpnIndicesRow

    @Query("SELECT COUNT(*) FROM jpn_sentences")
    abstract fun countSentences(): Int

    @Query("SELECT COUNT(*) FROM eng_translations")
    abstract fun countTranslations(): Int

    fun lookupEntries(query: JMdictQuery, limit: Int, offset: Int): List<JMdictRow> {
        return when (query) {
            is JMdictQuery.Like -> lookupJMdictRowsLike(query.likeText, limit, offset)
            is JMdictQuery.Exact -> lookupJMdictRowsExact(query.queryText)
            is JMdictQuery.EnglishMatch ->
                lookupJMdictRowsEnglishMatch(query.englishText, limit, offset)
        }
    }
    fun lookupSentences(query: TatoebaQuery, limit: Int, offset: Int): List<Sentence> {
        return when (query) {
            is TatoebaQuery.Japanese ->
                lookupSentencesWithJapaneseQuery(query, limit, offset)
            is TatoebaQuery.English ->
                lookupSentencesWithEnglishQuery(query, limit, offset)
        }
    }

}