package com.gaumala.openjisho.backend.setup

import com.gaumala.openjisho.common.SetupStep

/**
 * Special ProgressReporter for reading files line by line.
 */
class LinesReadReporter(private val delegate: ProgressReporter,
                        private val step: SetupStep,
                        private val lineCount: Int) {
    var linesRead = 0

    fun reportLine() {
        linesRead += 1
        delegate.updateProgress(step, linesRead * 100 / lineCount)
    }

    fun assertStillActive() {
        delegate.assertStillActive()
    }
}