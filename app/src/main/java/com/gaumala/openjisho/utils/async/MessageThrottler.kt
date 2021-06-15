package com.gaumala.openjisho.utils.async

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking

class MessageThrottler<T>(private val scope: CoroutineScope,
                          private val receiver: Receiver<T>,
                          private val interval: Long) {
    private val channel = Channel<T>(Channel.CONFLATED)

    init {
        startListening()
    }

    private suspend fun waitForNextMsg(): T? = withTimeoutOrNull(interval) {
        channel.receive()
    }

    private fun startListening() {
        scope.launch(Dispatchers.Default) {
            while(isActive) {
                var lastMsg: T = channel.receive()
                var maybeNextMsg: T? = waitForNextMsg()

                while (maybeNextMsg != null) {
                    lastMsg = maybeNextMsg
                    maybeNextMsg = waitForNextMsg()
                }

                scope.launch(Dispatchers.Main) {
                    receiver.handleMessage(lastMsg)
                }
            }
        }
    }

    fun sendMessage(msg: T) {
        // conflated chanel never blocks
        channel.sendBlocking(msg)
    }

    interface Receiver<T> {
        fun handleMessage(msg: T)
    }

}