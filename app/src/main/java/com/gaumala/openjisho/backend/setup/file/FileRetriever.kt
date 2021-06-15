package com.gaumala.openjisho.backend.setup.file

import com.gaumala.openjisho.backend.setup.ProgressReporter
import java.io.File

/**
 * Interface for objects that can retrieve files from anywhere. Doesn't matter if
 * it's from a remote server or inside the device. Implementation should abstract
 * away all those details and provide the requested file ready to be consumed.
 */
interface FileRetriever {
    /**
     * Suspends coroutine until the file is ready to be consumed.
     *
     * The name of the output file is private and belongs only to the implementation.
     * The implementation should be able to determine if the file to retrieve already
     * exists and return that without doing any additional work.
     * @param reporter An object that can report the tasks progress and
     * check if the user is still waiting for it.
     */
    suspend fun retrieve(reporter: ProgressReporter): File
}