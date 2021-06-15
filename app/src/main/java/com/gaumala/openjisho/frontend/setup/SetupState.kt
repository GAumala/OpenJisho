package com.gaumala.openjisho.frontend.setup

import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.common.SetupStep

sealed class SetupState {
    data class Working(val step: SetupStep, val progress: Int) : SetupState()
    object Done : SetupState()
    data class Error(val text: UIText): SetupState()
}