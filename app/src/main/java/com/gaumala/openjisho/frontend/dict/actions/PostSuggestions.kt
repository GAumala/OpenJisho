package com.gaumala.openjisho.frontend.dict.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.dict.*

class PostSuggestions (
    private val queryText: String,
    private val suggestions: List<String>
): Action<DictState, DictSideEffect>() {

    override fun update(state: DictState): Update<DictState, DictSideEffect> {
        val entryResults = state.entryResults as? EntryResults.Ready ?: return Update(state)
        if (entryResults.queryText != queryText) return Update(state)
        if (suggestions.isEmpty()) return Update(state)

        val newItem = EntryResult.Suggestion(queryText, suggestions)
        val newResults = entryResults.copy(
            items = entryResults.items.plus(newItem)
        )
        val newState = state.copy(entryResults = newResults)
        return Update(newState)
    }
}