package com.gaumala.openjisho.frontend.radicals

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.common.KanjiStrokesTuple

object RadicalsQueries {
    private fun RadicalIndex.updateButtonStateWithNewCombination(
        dao: DictQueryDao,
        newCombination: List<String>): RadicalIndex {
        if (buttonState == RadicalButtonState.selected)
            return this

        if (buttonState == RadicalButtonState.disabled)
            return this

        val possibleCombination = newCombination.plus(key)
        val possibleCount = dao.countKanjiByRadicals(
            possibleCombination, possibleCombination.size)

        if (possibleCount > 0)
            return this

        return copy(buttonState = RadicalButtonState.disabled)
    }

    private fun RadicalIndex.updateButtonStateWithZeroResults(): RadicalIndex {
        if (buttonState == RadicalButtonState.selected)
            return this

        return copy(buttonState = RadicalButtonState.disabled)
    }

    fun searchKanjiWithRadicalCombination(
        dao: DictQueryDao,
        combination: List<String>,
        radicalStateList: List<RadicalIndex>): Pair<List<KanjiStrokesTuple>, List<RadicalIndex>> {
        val tuples = dao.lookupKanjiByRadicals(combination, combination.size)
        return if (tuples.isEmpty()) {
            val newRadicals = radicalStateList.map {
                it.updateButtonStateWithZeroResults()
            }
            Pair(emptyList(), newRadicals)

        } else {
            val newRadicals = radicalStateList.map {
                it.updateButtonStateWithNewCombination(
                    dao, combination)
            }
            Pair(tuples, newRadicals)
        }
    }
}