package com.gaumala.openjisho.frontend.dict

import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.common.UIText

sealed class DictSearchResult {
    data class Entries(val queryText: String,
                       val canLoadMore: Boolean,
                      val list: List<EntryResult>): DictSearchResult()
    data class Sentences(val queryText: String,
                         val canLoadMore: Boolean,
                         val list:List<Sentence>): DictSearchResult()
    data class Error(val queryText: String,
                val message: UIText,
                val isSentence: Boolean): DictSearchResult()
}