package com.gaumala.openjisho.frontend.radicals.actions

import com.gaumala.openjisho.frontend.radicals.RadicalsSideEffect
import com.gaumala.openjisho.frontend.radicals.RadicalsState
import com.gaumala.mvi.Action
import com.gaumala.mvi.Update

class DeleteKanji: Action<RadicalsState, RadicalsSideEffect>() {

    override fun update(state: RadicalsState)
            : Update<RadicalsState, RadicalsSideEffect> {

        val len = state.queryText.length
        if (len == 0)
            return Update(state)

        val newState = state.copy(
            queryText = state.queryText.substring(0, len - 1))
        return Update(newState)
    }
}
