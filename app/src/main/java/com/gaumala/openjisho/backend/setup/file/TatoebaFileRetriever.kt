package com.gaumala.openjisho.backend.setup.file

import com.gaumala.openjisho.backend.setup.Checkpoint
import com.gaumala.openjisho.backend.setup.ProgressReporter
import com.gaumala.openjisho.backend.setup.SetupCheckpointManager
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.utils.ObservableInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * A [[FileRetriever]] implementation for downloading files from the
 * Tatoeba HTTP server.
 *
 * Tatoeba files are pretty large. Downloading and decompressing
 * can take a significant amount of time so this class reports progress
 * and manages checkpoints for each step.
 */
class TatoebaFileRetriever(private val cacheDir: File,
                           private val downloadPath: String,
                           private val isTarball: Boolean,
                           private val retrievedCheckpoint: Checkpoint,
                           private val downloadStep: SetupStep,
                           private val decompressStep: SetupStep,
                           private val checkpointManager: SetupCheckpointManager,
                           private val expectedSize: Long): FileRetriever {
    private val url = "https://downloads.tatoeba.org$downloadPath"

    private fun decompressTar(reporter: ProgressReporter,
                              compressedFile: File,
                              outputFile: File) {
        val contentLength = compressedFile.length()
        val assertStillActive = { reporter.assertStillActive() }
        BufferedInputStream(compressedFile.inputStream()).use { input ->
            val tarStream =
                TarArchiveInputStream(
                    BZip2CompressorInputStream(
                        ObservableInputStream(input, assertStillActive) {
                            val progress = 100 * it / contentLength
                            reporter.updateProgress(decompressStep, progress.toInt())
                        })
                )

            // Assume that there is only one file in the tar ball.
            // Extract it and exit.
            var entry = tarStream.nextTarEntry
            while (entry?.isDirectory == true)
                entry = tarStream.nextTarEntry


            tarStream.use { tarInput ->
                FileOutputStream(outputFile).use { output ->
                    tarInput.copyTo(output)
                }
            }
        }
    }

    private fun decompress(reporter: ProgressReporter,
                           compressedFile: File,
                           outputFile: File) {
        val contentLength = compressedFile.length()

        BufferedInputStream(compressedFile.inputStream(), 1024 * 1024).use { input ->
            val assertStillActive = { reporter.assertStillActive() }
            val bzipStream = BZip2CompressorInputStream(
                ObservableInputStream(input, assertStillActive) {
                    val progress = 100 * it / contentLength
                    reporter.updateProgress(decompressStep, progress.toInt())
                })

            bzipStream.use { tarInput ->
                FileOutputStream(outputFile).use { output ->
                    tarInput.copyTo(output)
                }
            }
        }
    }

    private fun alreadyDecompressed() =
        checkpointManager.reachedCheckpoint(retrievedCheckpoint)

    override suspend fun retrieve(reporter: ProgressReporter): File {
        val baseName = File(downloadPath).nameWithoutExtension
        val compressedFile = File(cacheDir, "$baseName.bz2")
        val outputFile = File(cacheDir, "$baseName.txt")

        val httpDownloader = HttpDownloader(
            url = url,
            expectedSize = expectedSize,
            assertStillActive = { reporter.assertStillActive() },
            onProgressUpdate = { progress ->
                reporter.updateProgress(downloadStep, progress)
            })

        if (alreadyDecompressed() && outputFile.exists())
            return outputFile

        try {
            httpDownloader.download(compressedFile)

            if (isTarball)
                decompressTar(reporter, compressedFile, outputFile)
            else
                decompress(reporter, compressedFile, outputFile)

            checkpointManager.markCheckpoint(retrievedCheckpoint, true)
            compressedFile.delete()
        } catch (ex: IOException) {
            compressedFile.delete()
            outputFile.delete()
        }

        return outputFile
    }

    companion object {
        fun sentences(cacheDir: File,
                      checkpointManager: SetupCheckpointManager
        ): FileRetriever {
            return TatoebaFileRetriever(
                cacheDir = cacheDir,
                downloadPath = "/exports/per_language/jpn/jpn_sentences.tsv.bz2",
                isTarball = false,
                retrievedCheckpoint = Checkpoint.sentencesRetrieved,
                downloadStep = SetupStep.downloadingTatoebaSentences,
                decompressStep = SetupStep.decompressingTatoebaSentences,
                checkpointManager = checkpointManager,
                expectedSize = 3 * 1024 * 1024
            )
        }

        fun translations(cacheDir: File,
                         checkpointManager: SetupCheckpointManager
        ): FileRetriever {
            return TatoebaFileRetriever(
                cacheDir = cacheDir,
                downloadPath = "/exports/per_language/eng/eng_sentences.tsv.bz2",
                isTarball = false,
                retrievedCheckpoint = Checkpoint.translationsRetrieved,
                downloadStep = SetupStep.downloadingTatoebaTranslations,
                decompressStep = SetupStep.decompressingTatoebaTranslations,
                checkpointManager = checkpointManager,
                expectedSize = 16 * 1024 * 1024
            )
        }

        fun indices(cacheDir: File,
                    checkpointManager: SetupCheckpointManager
        ): FileRetriever {
            return TatoebaFileRetriever(
                cacheDir = cacheDir,
                downloadPath = "/exports/jpn_indices.tar.bz2",
                isTarball = true,
                retrievedCheckpoint = Checkpoint.indicesRetrieved,
                downloadStep = SetupStep.downloadingTatoebaIndices,
                decompressStep = SetupStep.decompressingTatoebaIndices,
                checkpointManager = checkpointManager,
                expectedSize = 3 * 1024 * 1024
            )
        }
    }


}