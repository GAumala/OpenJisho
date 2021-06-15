package com.gaumala.openjisho.backend.setup.jmdict

import com.gaumala.openjisho.backend.db.*
import com.gaumala.openjisho.backend.setup.file.FileRetriever
import com.gaumala.openjisho.backend.setup.Checkpoint
import com.gaumala.openjisho.backend.setup.ProgressReporter
import com.gaumala.openjisho.backend.setup.SetupCheckpointManager
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.utils.countLines
import com.gaumala.openjisho.utils.error.Either

class JMdictDBSetup(private val downloader: FileRetriever,
                    private val checkpointManager: SetupCheckpointManager,
                    private val dao: SetupDao,
                    private val reporter: ProgressReporter
) {

    private val entryBatch = object : BatchInsert<JMdictRow>(999 / 5) {
        override fun runBatchInsert(batch: List<JMdictRow>) {
            dao.insertEntries(batch)
        }
    }

    private val jpnKeywordBatch = object : BatchInsert<JpnKeywordRow>(999 / 2) {
        override fun runBatchInsert(batch: List<JpnKeywordRow>) {
            entryBatch.flush()
            dao.insertJpnKeywords(batch)
        }
    }

    private val engKeywordBatch = object : BatchInsert<EngKeywordRow>(999 / 2) {
        override fun runBatchInsert(batch: List<EngKeywordRow>) {
            entryBatch.flush()
            dao.insertEngKeywords(batch)
        }
    }

    private val tagBatch = object : BatchInsert<TagRow>(999 / 2) {
        override fun runBatchInsert(batch: List<TagRow>) {
            entryBatch.flush()
            dao.insertTags(batch)
        }
    }

    private suspend fun insertEntries() {
        val file = downloader.retrieve(reporter)
        val totalLines = file.countLines()

        reporter.updateProgress(SetupStep.clearingJMdictTable, -1)

        dao.runInTransaction(Runnable {
            dao.deleteAllEntries()
            JMdictParser.exec(file) { entry, readLines ->
                reporter.assertStillActive()

                val (entryRow, jpnKeywordRows, engKeywordRows, tagRows) =
                    JMdictConverter.toDBRows(entry)
                entryBatch.insert(entryRow)
                jpnKeywordBatch.insert(jpnKeywordRows)
                engKeywordBatch.insert(engKeywordRows)
                tagBatch.insert(tagRows)
                reporter.updateProgress(SetupStep.insertingJMdictEntries,
                    readLines * 100 / totalLines)
            }
            tagBatch.flush()
            checkpointManager.markCheckpoint(
                Checkpoint.jmdictReady, true)
        })
    }

    suspend fun exec(): Either<Exception, Unit> {
        val alreadyStoredEntries =
            checkpointManager.reachedCheckpoint(Checkpoint.jmdictReady)

        return try {
            if (!alreadyStoredEntries)
                insertEntries()
            Either.Right(Unit)
        } catch (ex: Exception) {
            Either.Left(ex)
        } finally {
            reporter.dismiss()
        }
    }

}
