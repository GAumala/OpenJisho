package com.gaumala.openjisho.backend.setup.kanjidic

import com.gaumala.openjisho.backend.db.BatchInsert
import com.gaumala.openjisho.backend.db.KanjidicRow
import com.gaumala.openjisho.backend.db.SetupDao
import com.gaumala.openjisho.backend.setup.file.FileRetriever
import com.gaumala.openjisho.backend.setup.Checkpoint
import com.gaumala.openjisho.backend.setup.ProgressReporter
import com.gaumala.openjisho.backend.setup.SetupCheckpointManager
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.utils.countLines
import com.gaumala.openjisho.utils.error.Either

class KanjidicDBSetup(private val downloader: FileRetriever,
                      private val checkpointManager: SetupCheckpointManager,
                      private val dao: SetupDao,
                      private val reporter: ProgressReporter) {

    private val entryBatch = object : BatchInsert<KanjidicRow>(999 / 2) {
        override fun runBatchInsert(batch: List<KanjidicRow>) {
            dao.insertKanjiEntries(batch)
        }
    }

    private suspend fun insertKanji() {
        val file = downloader.retrieve(reporter)
        val totalLines = file.countLines()

        dao.runInTransaction(Runnable {
            reporter.updateProgress(SetupStep.clearingKanjiTable, -1)
            dao.deleteAllKanji()

            KanjidicParser.exec(file) { entry, readLines ->
                reporter.assertStillActive()

                if (entry.onReadings.isNotEmpty() || entry.kunReadings.isNotEmpty()) {
                    val entryRow = KanjidicConverter.toDBRow(entry)
                    entryBatch.insert(entryRow)
                }
                reporter.updateProgress(SetupStep.insertingKanji, readLines
                        * 100 / totalLines)
            }
            entryBatch.flush()
            checkpointManager.markCheckpoint(
                Checkpoint.kanjidicReady, true)
        })
    }

    suspend fun exec(): Either<Exception, Unit> {
        val alreadyStoredKanji =
            checkpointManager.reachedCheckpoint(Checkpoint.kanjidicReady)

        return try {
            if (!alreadyStoredKanji)
                insertKanji()
            Either.Right(Unit)
        } catch (ex: Exception) {
            Either.Left(ex)
        } finally {
            reporter.dismiss()
        }
    }
}