package com.gaumala.openjisho.backend.setup.file

import android.content.res.Resources
import com.gaumala.openjisho.R
import com.gaumala.openjisho.backend.setup.ProgressReporter
import com.gaumala.openjisho.common.SetupStep
import java.io.File
import java.util.zip.GZIPInputStream

/**
 * A [[FileRetriever]] implementation that can retrieve files from the app's
 * raw resources.
 *
 * The "monash" files are bundled with app resources becase the monash ftp server
 * that used to provide them has been shut down. So there's a static constructor
 * to create a retriever for each file.
 *
 * Retrieval should be relatively fast as the source is inside the device and all of
 * these files are pretty small so there are no checkpoints marked here and progress
 * is set to indeterminate.
 */
class RawResourceFileRetriever(resources: Resources,
                               private val cacheDir: File,
                               private val baseName: String,
                               private val step: SetupStep,
                               resId: Int): FileRetriever {
    private val loader = RawResourceLoader(resources, resId)

    private fun decompress(compressedFile: File, outputFile: File) {
        val inputStream = GZIPInputStream(compressedFile.inputStream())
        val outputStream = outputFile.outputStream()
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    override suspend fun retrieve(reporter: ProgressReporter): File {
        val compressedFile = File(cacheDir, "$baseName.gz")
        val outputFile = File(cacheDir, "$baseName.txt")

        reporter.updateProgress(step, -1)
        loader.loadToFile(compressedFile)

        try {
            decompress(compressedFile, outputFile)
            return outputFile
        } finally {
            compressedFile.delete()
        }
    }

    companion object {
        fun radkfile(resources: Resources, cacheDir: File) =
            RawResourceFileRetriever(
                resources,
                cacheDir,
                "radkfile",
                SetupStep.loadingRadkfile,
                R.raw.radkfile)

        fun kanjidic(resources: Resources, cacheDir: File) =
            RawResourceFileRetriever(
                resources,
                cacheDir,
                "kanjidic",
                SetupStep.loadingKanjidic,
                R.raw.kanjidic)

        fun jmdict(resources: Resources, cacheDir: File) =
            RawResourceFileRetriever(
                resources,
                cacheDir,
                "JMdict_e",
                SetupStep.loadingJMdict,
                R.raw.jmdict_e)
    }

}