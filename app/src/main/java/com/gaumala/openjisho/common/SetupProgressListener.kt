package com.gaumala.openjisho.common

interface SetupProgressListener {
    fun onProgressChange(step: SetupStep, progress: Int)
    fun onDismissed()
}