package com.gaumala.openjisho.frontend.radicals.actions

import com.gaumala.openjisho.frontend.radicals.RadicalsSideEffect
import com.gaumala.openjisho.frontend.radicals.RadicalsState
import com.gaumala.mvi.Action
import com.gaumala.mvi.Update

class AppendKanji(val newKanji: String)
    : Action<RadicalsState, RadicalsSideEffect>() {

    override fun update(state: RadicalsState)
            : Update<RadicalsState, RadicalsSideEffect> {

        val newState = state.copy(
            queryText = state.queryText + newKanji)
        return Update(newState)
    }
}