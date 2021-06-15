package com.gaumala.openjisho.backend.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SetupDao {
    @Insert
    fun insertEntries(entries: List<JMdictRow>)
    @Insert
    fun insertKanjiEntries(entries: List<KanjidicRow>)
    @Insert
    fun insertRadicalEntries(sentences: List<RadicalRow>)
    @Insert
    fun insertJpnKeywords(jpnKeywords: List<JpnKeywordRow>)
    @Insert
    fun insertEngKeywords(jpnKeywords: List<EngKeywordRow>)
    @Insert
    fun insertTags(tags: List<TagRow>)
    @Insert
    fun insertJpnSentences(sentences: List<JpnSentenceRow>)
    @Insert
    fun insertJpnIndices(sentences: List<JpnIndicesRow>)
    @Insert
    fun insertEngTranslations(translations: List<EngTranslationRow>)

    @Query("DELETE from jmdict")
    fun deleteAllEntries()
    @Query("DELETE from kanjidic")
    fun deleteAllKanji()
    @Query("DELETE FROM radicals")
    fun deleteAllRadicals()
    @Query("DELETE from jpn_sentences")
    fun deleteAllSentences()
    @Query("DELETE from jpn_indices")
    fun deleteAllJpnIndices()
    @Query("DELETE from eng_translations")
    fun deleteAllTranslations()

    @Query("SELECT id FROM jpn_sentences")
    fun getJapaneseSentenceIds(): List<Long>
    @Query("SELECT id FROM jpn_sentences")
    fun getSentenceLinks(): List<Long>
    @Query("DELETE from jpn_sentences where id in (:ids)")
    fun deleteSentencesById(ids: List<Long>)

    @Transaction
    fun runInTransaction(runnable: Runnable) {
        runnable.run()
    }
}