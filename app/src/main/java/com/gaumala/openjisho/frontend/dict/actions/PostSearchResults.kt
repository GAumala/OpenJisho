package com.gaumala.openjisho.frontend.dict.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.dict.*
import com.gaumala.openjisho.utils.recycler.PaginationStatus

data class PostSearchResults(val result: DictSearchResult)
    : Action<DictState, DictSideEffect>() {

    override fun update(state: DictState): Update<DictState, DictSideEffect> {
        return when (result) {
            is DictSearchResult.Entries -> {
               val currentEntries = state.entryResults
                if (currentEntries is EntryResults.Loading
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

            is DictSearchResult.Sentences -> {
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

            is DictSearchResult.Error -> {
                val newState = if (result.isSentence)
                    state.copy(
                        sentenceResults = SentenceResults.Error(
                            result.queryText,
                            result.message
                        )
                    )
                else {
                    if (result.suggestedQueries.isEmpty())
                        state.copy(
                            entryResults = EntryResults.Error(
                                result.queryText,
                                result.message
                            )
                        )
                    else // handle dict query suggestions as a special error
                        state.copy(
                            entryResults = EntryResults.ErrorWithSuggestions(
                                result.queryText,
                                result.suggestedQueries
                            )
                        )
                }
                return Update(newState)
            }
        }
    }
}