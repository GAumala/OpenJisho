package com.gaumala.openjisho.utils.async

import com.gaumala.openjisho.utils.error.Either
import kotlinx.coroutines.*

class CoroutineIOWorker(
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers = CoroutineDispatchers()
): AsyncWorker {

    override fun <T> workInBackground(workload: () -> T,
                                      callback: (Either<Exception, T>) -> Unit) {
        scope.launch(dispatchers.main) {
            val result = withContext(dispatchers.io) {
                try {
                    Either.Right<Exception, T>(workload())
                } catch (ex: Exception) {
                    Either.Left<Exception, T>(ex)
                }
            }

            callback(result)
        }

    }


}