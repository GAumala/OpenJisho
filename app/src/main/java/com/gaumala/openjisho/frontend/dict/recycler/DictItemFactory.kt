package com.gaumala.openjisho.frontend.dict.recycler

import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.frontend.dict.EntryResult
import com.gaumala.openjisho.frontend.dict.EntryResults
import com.gaumala.openjisho.frontend.dict.SentenceResults
import com.xwray.groupie.viewbinding.BindableItem


class DictItemFactory(private val isPicker: Boolean,
                      private val pushToHistory: (String) -> Unit,
                      private val onJMdictEntryClicked: (JMdictEntry.Summarized) -> Unit,
                      private val onKanjidicEntryClicked: (KanjidicEntry) -> Unit,
                      private val onSentenceClicked: (Sentence) -> Unit) {

    fun fromEntryResults(results: EntryResults): List<BindableItem<*>>  {
        return when (results) {
            EntryResults.Welcome -> listOf(
                if (isPicker) PickerWelcomeItem(false)
                else DictWelcomeItem(false)
            )
            is EntryResults.Loading -> listOf(LoadingItem())
            is EntryResults.Error -> listOf(ErrorItem(results.message))
            is EntryResults.Ready -> {
                if (results.results.isNotEmpty())
                    pushToHistory(results.queryText)
                val items = results.results.map {
                    when (it) {
                        is EntryResult.JMdict ->
                            DictJMdictItem(it.entry) {
                                onJMdictEntryClicked(it.entry)
                            }
                        is EntryResult.Kanjidic ->
                            DictKanjidicItem(it.entry) {
                                onKanjidicEntryClicked(it.entry)
                            }
                    }
                }
                if (results.shouldShowLoadingMore)
                    items.plus(LoadingMoreItem())
                else
                    items
            }
        }
    }

    fun fromSentenceResults(results: SentenceResults): List<BindableItem<*>>  {
        return when (results) {
            SentenceResults.Welcome -> listOf(
                if (isPicker) PickerWelcomeItem(true)
                else DictWelcomeItem(true)
            )
            is SentenceResults.Loading -> listOf(LoadingItem())
            is SentenceResults.Error -> listOf(ErrorItem(results.message))
            is SentenceResults.Ready -> {
                if (results.results.isNotEmpty())
                    pushToHistory(results.queryText)
                val items = results.results.map {
                    DictSentenceItem(it) { onSentenceClicked(it) }
                }

                if (results.shouldShowLoadingMore)
                    items.plus(LoadingMoreItem())
                else
                    items
            }
        }
    }
}