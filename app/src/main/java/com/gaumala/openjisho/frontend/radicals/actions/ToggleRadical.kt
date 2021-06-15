package com.gaumala.openjisho.frontend.radicals.actions

import com.gaumala.openjisho.frontend.radicals.*
import com.gaumala.mvi.Action
import com.gaumala.mvi.Update

class ToggleRadical(private val clickedRadical: RadicalIndex)
    : Action<RadicalsState, RadicalsSideEffect>() {

    private fun RadicalIndex.isDisabled(): Boolean {
        return buttonState == RadicalButtonState.disabled
    }

    private fun RadicalIndex.isToggledRadical(): Boolean {
        return unicodeChar == clickedRadical.unicodeChar
    }

    private fun removeToggledRadical(radicals: List<RadicalIndex>)
            : List<RadicalIndex> {
        return radicals.map {
            when {
                it.isToggledRadical() ->
                    clickedRadical.copy(
                        buttonState = RadicalButtonState.enabled)
                it.isDisabled() ->
                    // We have to recalculate all disabled radicals
                    // so enable everything temporarily
                    it.copy(buttonState = RadicalButtonState.enabled)
                else -> it
            }
        }
    }
    private fun addToggledRadical(radicals: List<RadicalIndex>)
            : List<RadicalIndex> {
        return radicals.map {
            when {
                it.isToggledRadical() ->
                    clickedRadical.copy(
                        buttonState = RadicalButtonState.selected)
                else -> it
            }
        }
    }

    private fun updateWithNewCombination(state: RadicalsState,
                                         addToCombination: Boolean)
            : Update<RadicalsState, RadicalsSideEffect> {
        val newRadicals = if (addToCombination)
                              addToggledRadical(state.radicals)
                          else
                              removeToggledRadical(state.radicals)

        val newCombination = newRadicals
            .filter { it.buttonState == RadicalButtonState.selected }
            .map { it.key }

        if (newCombination.isEmpty()) {
            return Update(state.copy(
                radicals = newRadicals,
                results = KanjiResults.Ready(emptyList())))
        }

        val sideEffect = RadicalsSideEffect.SearchKanji(
                                newRadicals, newCombination)
        val newState = state.copy(
            radicals = newRadicals,
            results = KanjiResults.Loading)
        return Update(newState, sideEffect)
    }

    override fun update(state: RadicalsState)
            : Update<RadicalsState, RadicalsSideEffect> {
        return when (clickedRadical.buttonState) {
            RadicalButtonState.disabled -> Update(state)
            RadicalButtonState.enabled ->
                updateWithNewCombination(state, true)
            RadicalButtonState.selected ->
                updateWithNewCombination(state, false)
        }
    }


}