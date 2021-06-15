package com.gaumala.openjisho.frontend.dict.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.dict.*

data class RunQuery(val queryText: String,
                    val lookupSentences: Boolean,
                    val shouldThrottle: Boolean)
    : Action<DictState, DictSideEffect>() {

    override fun update(state: DictState): Update<DictState, DictSideEffect> {
        return if (lookupSentences) {
            val loadedSentences = state.sentenceResults
            if (loadedSentences is SentenceResults.Ready
                && loadedSentences.queryText == queryText) {
                return Update(state)
            }
            if (loadedSentences is SentenceResults.Error
                && loadedSentences.queryText == queryText) {
                return Update(state)
            }

            val offset =
                if (loadedSentences is SentenceResults.Ready
                    && loadedSentences.queryText == queryText)
                    loadedSentences.nextOffset
                else 0
            val sideEffect = DictSideEffect.Search(
                DictSearchParams(queryText, lookupSentences, offset),
                shouldThrottle)
            val newState = state.copy(
                sentenceResults = SentenceResults.Loading(queryText))
            Update(newState, sideEffect)
        } else {

            val loadedEntries = state.entryResults
            if (loadedEntries is EntryResults.Ready
                && loadedEntries.queryText == queryText) {
                return Update(state)
            }

            if (loadedEntries is EntryResults.Error
                && loadedEntries.queryText == queryText) {
                return Update(state)
            }

            // search entries
            val offset =
                if (loadedEntries is EntryResults.Ready
                    && loadedEntries.queryText == queryText)
                    loadedEntries.nextOffset
                else 0
            val sideEffect = DictSideEffect.Search(
                DictSearchParams(queryText, lookupSentences, offset),
                shouldThrottle)
            val newState = state.copy(
                entryResults = EntryResults.Loading(queryText))
            Update(newState, sideEffect)
        }
    }
}
