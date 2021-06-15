package com.gaumala.openjisho.backend.setup

import com.gaumala.openjisho.common.SetupStep

/**
 * Interface for objects that can report progress to the user.
 * Objects that do heavy work should call these methods to
 * periodically to submit their work progress.
 */
interface ProgressReporter {
    /**
     * Call this every time the worker moves forward towards completion
     * of its current task. The caller is responsible of calculating the
     * current progress but the implementation is responsible of figuring
     * out if the progress value was already submitted.
     */
    fun updateProgress(step: SetupStep, newProgress: Int)

    /**
     * Throws an exception if user is no longer waiting for this task.
     * Long tasks should call this method often to stop execution as soon as
     * possible if the task was cancelled.
     */
    fun assertStillActive()

    /**
     * Call this method when the worker has finished its task so
     * that ProgressController can give attention to different
     * worker's progress. Once this method is called, the
     * implementation should ignore any subsequent updateProgress()
     * calls.
     *
     * This method must be called. ALWAYS. If it's not called, then
     * the progress controller could potentially wait forever and
     * never finish the setup.
     */
    fun dismiss()
}