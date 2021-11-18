package com.gaumala.openjisho.frontend.dict.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.dict.*
import com.gaumala.openjisho.utils.recycler.PaginationStatus

data class PostSearchResults(val result: DictSearchResult)
    : Action<DictState, DictSideEffect>() {

    override fun update(state: DictState): Update<DictState, DictSideEffect> {
        return when (result) {
            is DictSearchResult.Entries ->
               updateWithEntries(state, result)

            is DictSearchResult.Sentences ->
                updateWithSentences(state, result)

            is DictSearchResult.Error ->
                updateWithError(state, result)
        }
    }

    private fun updateWithSentences(
        state: DictState,
        result: DictSearchResult.Sentences
    ): Update<DictState, DictSideEffect> {
        val currentSentences = state.sentenceResults
        val newState = if (currentSentences is SentenceResults.Loading
            && currentSentences.queryText == result.queryText) {
            val pagination =
                if (result.canLoadMore)
                    PaginationStatus.canLoadMore
                else
                    PaginationStatus.complete
            val newSentences = SentenceResults.Ready(
                result.queryText, pagination, result.list
            )
            state.copy(sentenceResults = newSentences)
        }

        else if (currentSentences is SentenceResults.Ready
            && currentSentences.isLoadingMoreWith(result.queryText))
            state.copy(
                sentenceResults = currentSentences.addPage(
                    result.list, result.canLoadMore
                )
            )
        else
            state
        return Update(newState)
    }

    private fun updateWithEntries(
        state: DictState,
        result: DictSearchResult.Entries
    ): Update<DictState, DictSideEffect> {
        val currentEntries = state.entryResults
        return if (currentEntries is EntryResults.Loading
            && currentEntries.queryText == result.queryText) {
            // We are setting the first page of dict entries
            val pagination =
                if (result.canLoadMore)
                    PaginationStatus.canLoadMore
                else
                    PaginationStatus.complete
            val newEntries = EntryResults.Ready(
                result.queryText, pagination, result.list
            )
            // Add a side effect to load suggestions
            val jmDictEntries = newEntries.items
                .filterIsInstance<EntryResult.JMdict>()
            val sideEffect: DictSideEffect =
                DictSideEffect.GetSuggestions(
                    result.queryText, jmDictEntries
                )

            val newState = state.copy(entryResults = newEntries)
            Update(newState, sideEffect)

        } else if (currentEntries is EntryResults.Ready
            && currentEntries.isLoadingMoreWith(result.queryText)) {
            // We are appending a new page of dict entries
            val newState = state.copy(
                entryResults = currentEntries.addPage(
                    result.list, result.canLoadMore
                )
            )
            Update(newState)

        } else
            Update(state)
    }

    private fun updateWithError(
        state: DictState,
        result: DictSearchResult.Error
    ): Update<DictState, DictSideEffect> {
        val newState = when {
            result.isSentence ->
                createSentencesErrorState(state, result)
            else ->
                createEntriesErrorState(state, result)
        }
        return Update(newState)
    }

    private fun createSentencesErrorState(
        state: DictState,
        error: DictSearchResult.Error
    ): DictState {
        return when (val sentenceResults = state.sentenceResults) {
            is SentenceResults.Loading -> {
                if (sentenceResults.queryText != error.queryText) state
                else state.copy(
                    sentenceResults = SentenceResults.Error(
                        error.queryText,
                        error.message
                    )
                )
            }
            is SentenceResults.Ready -> {
                if (sentenceResults.queryText != error.queryText) state
                else state.copy(
                    sentenceResults = SentenceResults.Error(
                        error.queryText,
                        error.message
                    )
                )
            }
            else -> return state
        }
    }

    private fun createEntriesErrorState(
        state: DictState,
        error: DictSearchResult.Error
    ): DictState {
        return when (val entryResults = state.entryResults) {
            is EntryResults.Loading -> {
                when {
                    entryResults.queryText != error.queryText -> state
                    error.suggestedQueries.isNotEmpty() -> {
                        state.copy(
                            entryResults = EntryResults.ErrorWithSuggestions(
                                error.queryText,
                                error.suggestedQueries
                            )
                        )
                    }
                    else -> state.copy(
                        entryResults = EntryResults.Error(
                            error.queryText,
                            error.message
                        )
                    )
                }
            }
            is EntryResults.Ready -> {
                when {
                    entryResults.queryText != error.queryText -> state
                    error.suggestedQueries.isNotEmpty() -> {
                        state.copy(
                            entryResults = EntryResults.ErrorWithSuggestions(
                                error.queryText,
                                error.suggestedQueries
                            )
                        )
                    }
                    else -> state.copy(
                        entryResults = EntryResults.Error(
                            error.queryText,
                            error.message
                        )
                    )
                }
            }
            else -> return state
        }
    }
}