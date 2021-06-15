package com.gaumala.openjisho.backend.setup

import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.common.SetupStep

interface SetupServiceListener {
    fun onProgressChange(step: SetupStep, progress: Int)
    fun onComplete(errorText: UIText?)
}