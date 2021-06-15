package com.gaumala.openjisho.backend.setup

import com.gaumala.openjisho.backend.db.SetupDao
import com.gaumala.openjisho.backend.setup.file.FileRetriever
import com.gaumala.openjisho.backend.setup.jmdict.JMdictDBSetup
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicDBSetup
import com.gaumala.openjisho.backend.setup.radkfile.RadkfileDBSetup
import com.gaumala.openjisho.backend.setup.tatoeba.TatoebaDBSetup
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.utils.error.Either
import kotlinx.coroutines.*
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*

/**
 * This class handles the app setup. It manages several coroutines that
 * concurrently download, and process dictionary files to populate the
 * database used for dictionary queries in the app.
 *
 * The [[FileRetriever]] classes provided in the constructor take care
 * of retrieving the file. This could be done from app resources or a
 * remote server. This class does not know about such details and it
 * does not perform any file cleanup after all tasks complete.
 */
class SetupWorker(private val reportProgressToService: (SetupStep, Int) -> Unit,
                  private val checkpointManager: SetupCheckpointManager,
                  private val radkfileRetriever: FileRetriever,
                  private val jmdictRetriever: FileRetriever,
                  private val kanjidicRetriever: FileRetriever,
                  private val tatoebaSentencesRetriever: FileRetriever,
                  private val tatoebaTranslationsRetriever: FileRetriever,
                  private val tatoebaIndicesRetriever: FileRetriever,
                  private val setupDao: SetupDao) {

    private suspend fun processDeferredResultQueue(
        queue: Queue<Deferred<Either<Exception, Unit>>>
    ): Either<Exception, Unit> {

        var errorResult: Either.Left<Exception, Unit>? = null
        while (queue.isNotEmpty()) {
            val pendingTask = queue.remove()
            val result = pendingTask.await()

            if (result is Either.Left) {
                // result.value.printStackTrace()
                errorResult = result
            }

            if (queue.isEmpty())
                return errorResult ?: result
        }

        throw IllegalArgumentException("No tasks found in queue")
    }

    private fun deferFile(scope: CoroutineScope,
                          retriever: FileRetriever,
                          reporter: ProgressReporter
    ): Deferred<Either<Exception, File>> {
        return scope.async {
            try {
                val file = retriever.retrieve(reporter)
                Either.Right<Exception, File>(file)
            } catch (ex: Exception) {
                Either.Left<Exception, File>(ex)
            } finally {
                reporter.dismiss()
            }
        }
    }

    suspend fun doWork(scope: CoroutineScope): Either<Exception, Unit> {
        // Initialize with 5 because there are going to be 5
        // coroutines reporting progress simultaneously, doing
        // the following tasks:
        // 1. Populate RADKFILE table
        // 2. Populate JMdict table
        // 3. Populate KANJIDIC table
        // 4. Download Tatoeba links file
        // 5. Download Tatoeba translations file
        // 6. Populate Tatoeba table
        val progressController = ProgressController(scope.assertStillActive, 6)
        progressController.manageReporters(scope, reportProgressToService)

        val radkfileReporter = progressController.getReporterWithPriority(0)
        val kanjidicReporter = progressController.getReporterWithPriority(1)
        val jmDictReporter = progressController.getReporterWithPriority(2)
        val tatoebaIndicesReporter = progressController.getReporterWithPriority(3)
        val tatoebaTranslationsReporter = progressController.getReporterWithPriority(4)
        val tatoebaReporter = progressController.getReporterWithPriority(5)

        val radkfilePendingRes = scope.async {
            val operation = RadkfileDBSetup(
                radkfileRetriever,
                checkpointManager,
                setupDao,
                radkfileReporter)
            operation.exec()
        }

        val kanjidicPendingRes = scope.async {
            val operation = KanjidicDBSetup(
                kanjidicRetriever,
                checkpointManager,
                setupDao,
                kanjidicReporter)
            operation.exec()
        }

        val jmDictPendingRes = scope.async {
            val operation = JMdictDBSetup(
                jmdictRetriever,
                checkpointManager,
                setupDao,
                jmDictReporter)
            operation.exec()
        }

        val deferredIndicesFile =
            deferFile(scope, tatoebaIndicesRetriever, tatoebaIndicesReporter)

        val deferredTranslationsFile =
            deferFile(scope, tatoebaTranslationsRetriever, tatoebaTranslationsReporter)

        val tatoebaPendingRes = scope.async {
            val operation = TatoebaDBSetup(
                tatoebaSentencesRetriever,
                deferredIndicesFile,
                deferredTranslationsFile,
                checkpointManager,
                setupDao,
                tatoebaReporter
            )
            operation.exec()
        }

        val pendingTasks: Queue<Deferred<Either<Exception, Unit>>> = LinkedList()
        pendingTasks.add(radkfilePendingRes)
        pendingTasks.add(kanjidicPendingRes)
        pendingTasks.add(jmDictPendingRes)
        pendingTasks.add(tatoebaPendingRes)

        return processDeferredResultQueue(pendingTasks).map {
            // if success clear all checkpoints
            checkpointManager.clearAll()
        }
    }

    private val CoroutineScope.assertStillActive: () -> Unit
        get () = {
            if (! isActive) throw CancellationException()
        }

}