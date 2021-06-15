package com.gaumala.openjisho.backend.setup

import com.gaumala.openjisho.common.SetupProgressListener
import com.gaumala.openjisho.common.SetupStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*

/**
 * An object that manages a pool of ProgressReporter instances. There
 * should one ProgressReporter instance per concurrent task.
 *
 * The pool is fully created at construction time, but you must call
 * manageReporters() before you can submit work progress because the
 * pool is managed internally via coroutines. After that use the method
 * getReporterWithPriority() to get a reporter instance that your worker
 * class can use.
 */
class ProgressController(assertCoroutineStillActive: () -> Unit,
                         totalConcurrentTasks: Int) {
    private val reporters: LinkedList<SetupProgressReporter> = LinkedList()
    private val channel = Channel<Msg>(Channel.CONFLATED)

    init {
        repeat(totalConcurrentTasks) {
            reporters.add(object: SetupProgressReporter() {
                override fun assertStillActive() {
                    assertCoroutineStillActive()
                }
            })
        }
    }

    private val activeReporterListener = object: SetupProgressListener {
        override fun onProgressChange(step: SetupStep, progress: Int) {
            channel.sendBlocking(Msg.Update(step, progress))
        }

        override fun onDismissed() {
            channel.sendBlocking(Msg.Dismiss)
        }
    }


    private fun handleReporterUpdate(step: SetupStep,
                                     progress: Int,
                                     onProgressChange: (SetupStep, Int) -> Unit) {
        onProgressChange(step, progress)
    }

    private fun handleReporterDismiss(onProgressChange: (SetupStep, Int) -> Unit) {
        while (reporters.isNotEmpty() && reporters.peek().isDismissed) {
            val dismissedReporter = reporters.remove()
            dismissedReporter.listener = null
        }

        // if a new reporter becomes active, show its current progress
        if (!reporters.isEmpty()) {
            val newActiveReporter = reporters.peek()
            val (step, progress) = newActiveReporter.currentProgress
            onProgressChange(step, progress)

            newActiveReporter.listener = activeReporterListener
        }
    }

    /**
     * Launches an new coroutine with the give scope that monitors
     * all ProgressReporter instances ensuring that only one of them
     * publishes it's progress to the main thread at a given time.
     *
     * @param scope The scope to use to launch the coroutine. This
     * should be tied to the service's lifecycle
     * @param onProgressChange a lambda that can publish progress
     * to the main thread. It should be aware that it may be called
     * from a background thread. It is guaranteed that only one reporter
     * has has access to this lambda and it is only freed when that
     * reporter is dismissed.
     */
    fun manageReporters(scope: CoroutineScope,
                        onProgressChange: (SetupStep, Int) -> Unit) {
        val activeReporter = reporters.peek()
        activeReporter.listener = activeReporterListener

        scope.launch {
            while (reporters.isNotEmpty() && isActive) {
                val msg = channel.receive()
                when (msg) {
                    is Msg.Update ->
                        handleReporterUpdate(
                            msg.step, msg.progress, onProgressChange)

                    is Msg.Dismiss ->
                        handleReporterDismiss(onProgressChange)
                }
            }
        }
    }

    /**
     * Get a reporter with the specified priority, which is an
     * integer between 0 and <totalConcurrentTasks>. Each priority
     * value returns a different instance. The lower the number,
     * the sooner that instance can publish its progress to the main
     * thread.
     *
     * For example a reporter with priority 0 can publish right
     * away, while a reporter with priority 3 will have to wait until
     * reporters 0,1 and 2 are dismissed.
     *
     * @throws IndexOutOfBoundsException if the priority number is
     * not within the specified bounds.
     */
    fun getReporterWithPriority(priority: Int): ProgressReporter {
        return reporters[priority]
    }

    private sealed class Msg {
        class Update(val step: SetupStep, val progress: Int): Msg()
        object Dismiss: Msg()
    }
}