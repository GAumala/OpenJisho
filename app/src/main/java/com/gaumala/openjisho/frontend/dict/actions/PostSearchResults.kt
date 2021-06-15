package com.gaumala.openjisho.frontend.dict.actions

import com.gaumala.openjisho.frontend.dict.DictSideEffect
import com.gaumala.openjisho.frontend.dict.DictState
import com.gaumala.openjisho.frontend.dict.SentenceResults
import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.dict.DictSearchResult
import com.gaumala.openjisho.frontend.dict.EntryResults
import com.gaumala.openjisho.utils.recycler.PaginationStatus

data class PostSearchResults(val result: DictSearchResult)
    : Action<DictState, DictSideEffect>() {

    override fun update(state: DictState): Update<DictState, DictSideEffect> {
        val newState = when (result) {
            is DictSearchResult.Entries -> {
               val currentEntries = state.entryResults
                if (currentEntries is EntryResults.Loading
                    && currentEntries.queryText == result.queryText) {

                    val pagination =
                        if (result.canLoadMore)
                            PaginationStatus.canLoadMore
                        else
                            PaginationStatus.complete
                    val newEntries = EntryResults.Ready(
                        result.queryText, pagination, result.list)
                    state.copy(entryResults = newEntries)
                }

                else if (currentEntries is EntryResults.Ready
                    && currentEntries.isLoadingMoreWith(result.queryText))
                    state.copy(entryResults =
                        currentEntries.addPage(
                            result.list, result.canLoadMore))
                else
                    state
            }

            is DictSearchResult.Sentences -> {
                val currentSentences = state.sentenceResults
                if (currentSentences is SentenceResults.Loading
                    && currentSentences.queryText == result.queryText) {
                    val pagination =
                        if (result.canLoadMore)
                            PaginationStatus.canLoadMore
                        else
                            PaginationStatus.complete
                    val newSentences = SentenceResults.Ready(
                        result.queryText, pagination, result.list)
                    state.copy(sentenceResults = newSentences)
                }

                else if (currentSentences is SentenceResults.Ready
                    && currentSentences.isLoadingMoreWith(result.queryText))
                    state.copy(sentenceResults =
                        currentSentences.addPage(
                            result.list, result.canLoadMore))
                else
                    state
            }

            is DictSearchResult.Error -> {
                if (result.isSentence)
                    state.copy(sentenceResults = SentenceResults.Error(
                        result.queryText, result.message))
                else
                    state.copy(entryResults = EntryResults.Error(
                        result.queryText, result.message))
            }
        }

        return Update(newState)
    }
}