package com.gaumala.openjisho.common

import android.content.Context
import androidx.annotation.StringRes
import com.gaumala.openjisho.R

enum class SetupStep {
    initializing,
    loadingRadkfile,
    loadingKanjidic,
    loadingJMdict,
    clearingRadicalsTable,
    insertingRadicals,
    clearingJMdictTable,
    insertingJMdictEntries,
    clearingKanjiTable,
    insertingKanji,
    downloadingTatoebaSentences,
    decompressingTatoebaSentences,
    downloadingTatoebaTranslations,
    decompressingTatoebaTranslations,
    downloadingTatoebaIndices,
    decompressingTatoebaIndices,
    clearingSentencesTable,
    insertingSentences,
    clearingIndicesTable,
    insertingIndices,
    findingTranslations,
    clearingTranslationsTable,
    insertingTranslations,
    wrappingUp;

    @StringRes
    fun toStringResId(): Int {
        return when (this) {
            initializing ->
                R.string.building_db
            loadingJMdict ->
                R.string.loading_jmdict
            loadingRadkfile ->
                R.string.loading_radkfile
            loadingKanjidic ->
                R.string.loading_kanjidic
            clearingRadicalsTable ->
                R.string.clearing_radicals_table
            insertingRadicals ->
                R.string.inserting_radicals
            clearingJMdictTable ->
                R.string.clearing_jmdict_table
            insertingJMdictEntries ->
                R.string.storing_jmdict_entries
            clearingKanjiTable ->
                R.string.clearing_kanjidic_table
            insertingKanji ->
                R.string.storing_kanjidic_entries
            downloadingTatoebaSentences ->
                R.string.downloading_tatoeba_sentences
            decompressingTatoebaSentences ->
                R.string.decompressing_tatoeba_sentences
            downloadingTatoebaTranslations ->
                R.string.downloading_tatoeba_translations
            decompressingTatoebaTranslations->
                R.string.decompressing_tatoeba_translations
            downloadingTatoebaIndices ->
                R.string.downloading_tatoeba_links
            decompressingTatoebaIndices ->
                R.string.decompressing_tatoeba_links
            clearingSentencesTable ->
                R.string.clearing_sentences_table
            insertingSentences ->
                R.string.storing_tatoeba_sentences
            findingTranslations ->
                R.string.looking_up_tatoeba_translations
            clearingTranslationsTable ->
                R.string.clearing_translations_table
            insertingTranslations ->
                R.string.storing_tatoeba_translations
            wrappingUp ->
                R.string.wrapping_up
            clearingIndicesTable ->
                R.string.clearing_indices_table
            insertingIndices ->
                R.string.storing_tatoeba_indices
        }
    }

    fun toString(ctx: Context): String =
        ctx.getString(toStringResId())
}
