package com.gaumala.openjisho.backend.setup

import com.gaumala.openjisho.common.SetupProgressListener
import com.gaumala.openjisho.common.SetupStep

/** An object that stores setup work progress and is able to report it
 * to ProgressController. The classes that do the actual work and want
 * to periodically submit their progresss should only interact with
 * the ProgressReporter interface. All the other
 * methods and fields are of exclusive use of ProgressController.
 */
abstract class SetupProgressReporter: ProgressReporter {

    private var currentStep: SetupStep = SetupStep.initializing
    private var currentPercentage = -1
    var isDismissed = false
    private set

    /**
     * ProgressController will set a listener to only on reporter at any
     * given time. That reporter is considered the active one and is
     * the only one allowed to send progress messages to ProgressController
     */
    var listener: SetupProgressListener? = null

    val currentProgress: Pair<SetupStep, Int>
            get() = Pair(currentStep, currentPercentage)

    override fun updateProgress(step: SetupStep, newProgress: Int) {
        if (isDismissed)
            return

        if (currentPercentage != newProgress || currentStep != step) {
            currentPercentage = newProgress
            currentStep = step
            listener?.onProgressChange(currentStep, currentPercentage)
        }
    }

    override fun dismiss() {
        isDismissed = true
        listener?.onDismissed()
    }
}