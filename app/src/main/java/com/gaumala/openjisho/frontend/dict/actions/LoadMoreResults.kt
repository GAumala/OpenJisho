package com.gaumala.openjisho.frontend.dict.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.dict.*
import com.gaumala.openjisho.utils.recycler.PaginationStatus

data class LoadMoreResults(val lookupSentences: Boolean)
    : Action<DictState, DictSideEffect>() {


    private fun loadMoreSentences(state: DictState): Update<DictState, DictSideEffect> {
        val loadedSentences = state.sentenceResults
        if (loadedSentences is SentenceResults.Ready
            && loadedSentences.pagination == PaginationStatus.canLoadMore) {
            val params = DictSearchParams(
                loadedSentences.queryText,
                true,
                loadedSentences.nextOffset
            )
            val newSentences = loadedSentences.copy(
                pagination = PaginationStatus.isLoadingMore)
            val sideEffect = DictSideEffect.Search(params, false)
            return Update(state.copy(sentenceResults = newSentences), sideEffect)
        }
        return Update(state)
    }

    private fun loadMoreEntries(state: DictState): Update<DictState, DictSideEffect> {
        val loadedEntries = state.entryResults
        if (loadedEntries is EntryResults.Ready
            && loadedEntries.pagination == PaginationStatus.canLoadMore) {
            val params = DictSearchParams(
                loadedEntries.queryText,
                false,
                loadedEntries.nextOffset
            )
            val newEntries = loadedEntries.copy(
                pagination = PaginationStatus.isLoadingMore)
            val sideEffect = DictSideEffect.Search(params, false)
            return Update(state.copy(entryResults = newEntries), sideEffect)
        }
        return Update(state)
    }

    override fun update(state: DictState): Update<DictState, DictSideEffect> {
        return if (lookupSentences)
            loadMoreSentences(state)
        else
            loadMoreEntries(state)
    }
}
