package com.gaumala.openjisho.utils

import com.gaumala.openjisho.utils.async.CoroutineDispatchers
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.io.IOException

interface AsyncFileHandler {
    fun write(getContent: () -> String)
    fun <T>read(transform: (String) -> T, onContentsReady: (T) -> Unit)

    class ChannelFileHandler(private val scope: CoroutineScope,
                             private val dispatchers: CoroutineDispatchers,
                             path: String): AsyncFileHandler {

        constructor(scope: CoroutineScope, path: String):
                this(scope, CoroutineDispatchers(), path)

        private val file = File(path)
        private val channel = Channel<() -> String>()

        init {
            scope.launch(dispatchers.io) {
                while (isActive) {
                    val getContent = channel.receive()
                    val content = getContent()
                    file.writeText(content)
                }
            }
        }

        override fun write(getContent: () -> String) {
            scope.launch(dispatchers.default) {
                channel.send(getContent)
            }
        }

        override fun <T> read(transform: (String) -> T,
                              onContentsReady: (T) -> Unit) {
            scope.launch(dispatchers.main) {
                val content = async(dispatchers.io) {
                    try {
                        transform(file.readText())
                    } catch (ex: IOException) {
                        transform("")
                    }
                }

                onContentsReady(content.await())
            }
        }

    }
}