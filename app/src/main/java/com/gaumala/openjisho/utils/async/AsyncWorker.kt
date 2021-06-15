package com.gaumala.openjisho.utils.async

import com.gaumala.openjisho.utils.error.Either

interface AsyncWorker {
    fun <T> workInBackground(workload: () -> T,
                             callback: (Either<Exception, T>) -> Unit)
}