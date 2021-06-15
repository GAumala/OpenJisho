package com.gaumala.openjisho.backend.setup.radkfile

import com.gaumala.openjisho.backend.db.BatchInsert
import com.gaumala.openjisho.backend.db.RadicalRow
import com.gaumala.openjisho.backend.db.SetupDao
import com.gaumala.openjisho.backend.setup.file.FileRetriever
import com.gaumala.openjisho.backend.setup.Checkpoint
import com.gaumala.openjisho.backend.setup.ProgressReporter
import com.gaumala.openjisho.backend.setup.SetupCheckpointManager
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.utils.countLines
import com.gaumala.openjisho.utils.error.Either

class RadkfileDBSetup(private val downloader: FileRetriever,
                      private val checkpointManager: SetupCheckpointManager,
                      private val dao: SetupDao,
                      private val reporter: ProgressReporter) {
    private val parser = RadkfileParser()

    private val radicalBatch = object : BatchInsert<RadicalRow>(999 / 2) {
        override fun runBatchInsert(batch: List<RadicalRow>) {
            dao.insertRadicalEntries(batch)
        }
    }
    private suspend fun insertRadicals() {
        val radicalsFile = downloader.retrieve(reporter)
        val totalLines = radicalsFile.countLines()

        dao.runInTransaction(Runnable {
            reporter.updateProgress(SetupStep.clearingRadicalsTable, -1)
            dao.deleteAllRadicals()

            parser.exec(radicalsFile) { radicalIndex, lineNumber ->
                reporter.assertStillActive()

                radicalBatch.insert(radicalIndex.toRows())
                val progress = lineNumber * 100 / totalLines
                reporter.updateProgress(
                    SetupStep.insertingRadicals, progress)
            }
            radicalBatch.flush()
        })

        checkpointManager.markCheckpoint(
            Checkpoint.radkfileReady, true)
    }



    suspend fun exec(): Either<Exception, Unit> {
        val alreadyStoredRadicals =
            checkpointManager.reachedCheckpoint(Checkpoint.radkfileReady)

        return try {
            if (!alreadyStoredRadicals)
                insertRadicals()
            Either.Right(Unit)
        } catch (ex: Exception) {
            Either.Left(ex)
        } finally {
            reporter.dismiss()
        }
    }
}