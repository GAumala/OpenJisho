package com.gaumala.openjisho.frontend.setup.actions

import com.gaumala.openjisho.common.SetupStep
import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.setup.SetupState

data class UpdateProgress(val step: SetupStep, val progress: Int)
    : Action<SetupState, Void>() {

    override fun update(state: SetupState): Update<SetupState, Void> {
        if (state !is SetupState.Working)
            return Update(state)

        return Update(SetupState.Working(step, progress))
    }
}
