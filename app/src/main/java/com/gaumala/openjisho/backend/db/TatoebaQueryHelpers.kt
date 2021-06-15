package com.gaumala.openjisho.backend.db

import com.gaumala.openjisho.backend.TatoebaQuery
import com.gaumala.openjisho.common.Sentence
import java.util.*
import kotlin.collections.ArrayList

fun DictQueryDao.lookupSentencesWithJapaneseQuery(query: TatoebaQuery.Japanese,
                                                  limit: Int,
                                                  offset: Int): List<Sentence> {
    val sentenceKeys =
        lookupSentenceIdsMatchingIndices(query.matchText, limit, offset)

    val sentences = lookupSentencesById(sentenceKeys)
    val translations = lookupTranslationsById(sentenceKeys)
    // convert to map to filter out duplicates
    val translationsMap = translations.map { Pair(it.japaneseId, it) }
        .toMap()

    return sentences.fold(ArrayList(limit)) { acc, row ->
        // database should guarantee that every
        // sentence has an English translation
        // but apparently it doesn't
        val translationRow = translationsMap[row.id]
        val english = translationRow?.english ?: "-"

        acc.add(Sentence(
            id = row.id,
            japanese = row.japanese,
            english = english))
        acc
    }
}

fun DictQueryDao.lookupSentencesWithEnglishQuery(query: TatoebaQuery.English,
                                                 limit: Int,
                                                 offset: Int): List<Sentence> {
    val translations = lookupTranslationsMatch(query.matchText, limit, offset)
    val translationsMap = translations
        .map { Pair(it.japaneseId, it) }
        .toMap()

    val sentences = lookupSentencesById(translationsMap.keys.toList())
    return sentences.fold(LinkedList()) { acc, row ->
        val translationRow = translationsMap[row.id]
        if (translationRow != null)
            acc.add(Sentence(
                id = row.id,
                japanese = row.japanese,
                english = translationRow.english))
        acc
    }
}
