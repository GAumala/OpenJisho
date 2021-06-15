package com.gaumala.openjisho.frontend.radicals.actions

import com.gaumala.openjisho.frontend.radicals.KanjiResults
import com.gaumala.openjisho.frontend.radicals.RadicalIndex
import com.gaumala.openjisho.frontend.radicals.RadicalsSideEffect
import com.gaumala.openjisho.frontend.radicals.RadicalsState
import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.common.KanjiStrokesTuple

class PostResults(val results: List<KanjiStrokesTuple>,
                  val radicals: List<RadicalIndex>)
    : Action<RadicalsState, RadicalsSideEffect>() {

    override fun update(state: RadicalsState)
            : Update<RadicalsState, RadicalsSideEffect> {

        val newState = state.copy(
            radicals = radicals,
            results = KanjiResults.Ready(results))
        return Update(newState)
    }

}