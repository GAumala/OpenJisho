package com.gaumala.openjisho.frontend.setup.actions

import com.gaumala.openjisho.common.UIText
import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.setup.SetupState

data class CompleteSetup(val errorText: UIText?): Action<SetupState, Void>() {

    override fun update(state: SetupState): Update<SetupState, Void> {
        if (state !is SetupState.Working)
            return Update(state)

        val newState =
            if (errorText != null)
                SetupState.Error(errorText)
            else SetupState.Done

        return Update(newState)
    }
}