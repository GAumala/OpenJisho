package com.gaumala.openjisho.frontend.setup.actions

import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.frontend.setup.SetupState
import com.gaumala.mvi.Action
import com.gaumala.mvi.Update

class RestartSetup(): Action<SetupState, Void>() {

    override fun update(state: SetupState): Update<SetupState, Void> {
        val newState =
            SetupState.Working(SetupStep.initializing, 0)
        return Update(newState)
    }
}
