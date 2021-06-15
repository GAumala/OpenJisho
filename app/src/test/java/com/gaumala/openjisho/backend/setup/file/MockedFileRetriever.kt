package com.gaumala.openjisho.backend.setup.file

import com.gaumala.openjisho.backend.setup.ProgressReporter
import com.gaumala.openjisho.common.SetupStep
import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException

class MockedFileRetriever(
    val targetFile: File,
    val loadTime: Long,
    val contents: String = "",
    val fileCharset: String = "UTF-8",
    val setupStep: SetupStep? = null,
    val errorMessage: String? = null
): FileRetriever {
    private val indeterminateSteps = listOf(
        SetupStep.loadingRadkfile,
        SetupStep.loadingJMdict,
        SetupStep.loadingKanjidic)

    override suspend fun retrieve(reporter: ProgressReporter): File {
        if (setupStep != null) {
            val progress = if (indeterminateSteps.contains(setupStep)) -1 else 1
            reporter.updateProgress(setupStep, progress)
        }

        delay(loadTime)
        if (errorMessage != null)
            throw IOException(errorMessage)

        targetFile.writeText(contents, charset(fileCharset))
        return targetFile
    }
}