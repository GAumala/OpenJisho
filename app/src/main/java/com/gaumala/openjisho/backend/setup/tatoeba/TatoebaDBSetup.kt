package com.gaumala.openjisho.backend.setup.tatoeba

import com.gaumala.openjisho.backend.db.*

import com.gaumala.openjisho.backend.setup.file.FileRetriever
import com.gaumala.openjisho.backend.setup.Checkpoint
import com.gaumala.openjisho.backend.setup.LinesReadReporter
import com.gaumala.openjisho.backend.setup.ProgressReporter
import com.gaumala.openjisho.backend.setup.SetupCheckpointManager
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.utils.countLines
import com.gaumala.openjisho.utils.error.Either
import kotlinx.coroutines.Deferred
import java.io.File

class TatoebaDBSetup(private val sentencesDownloader: FileRetriever,
                     private val deferredIndicesFile: Deferred<Either<Exception, File>>,
                     private val deferredTranslationsFile: Deferred<Either<Exception, File>>,
                     private val checkpointManager: SetupCheckpointManager,
                     private val dao: SetupDao,
                     private val reporter: ProgressReporter) {

    private val jpnSentenceBatch = object : BatchInsert<JpnSentenceRow>(999 / 2) {
        override fun runBatchInsert(batch: List<JpnSentenceRow>) {
            dao.insertJpnSentences(batch)
        }
    }
    private val jpnIndicesBatch = object : BatchInsert<JpnIndicesRow>(999 / 2) {
        override fun runBatchInsert(batch: List<JpnIndicesRow>) {
            dao.insertJpnIndices(batch)
        }
    }
    private val engTranslationBatch = object : BatchInsert<EngTranslationRow>(999 / 2) {
        override fun runBatchInsert(batch: List<EngTranslationRow>) {
            dao.insertEngTranslations(batch)
        }
    }

    private fun parseSentenceLine(line: String): TatoebaSentence {
        val array = line.split('\t')
        val id = array[0].toLong()
        val lang = TatoebaSentence.Lang.fromCode(array[1])
        val sentence = array[2]
        return TatoebaSentence(id, lang, sentence)
    }
    private fun parseRelationLine(line: String): TatoebaLink {
        val array = line.split('\t')
        val sentenceId = array[0].toLong()
        val translationId = array[1].toLong()
        val japaneseIndices = array[2]
        return TatoebaLink(sentenceId, translationId, japaneseIndices)
    }

    private fun deleteSentencesWithoutTranslations(japaneseSentenceIds: Set<Long>) {
        japaneseSentenceIds.chunked(999).forEach {
            reporter.assertStillActive()
            dao.deleteSentencesById(it)
        }
    }

    private fun insertJpnSentences(sentencesFile: File,
                                   japaneseSentenceIds: HashSet<Long>,
                                   fileReporter: LinesReadReporter
    ): HashSet<Long> {
        sentencesFile.forEachLine { line ->
            fileReporter.assertStillActive()

            val sentenceLine = parseSentenceLine(line)
            if (sentenceLine.lang == TatoebaSentence.Lang.jpn) {
                japaneseSentenceIds.add(sentenceLine.id)
                jpnSentenceBatch.insert(
                    JpnSentenceRow.fromParsedSentence(sentenceLine))
            }
            fileReporter.reportLine()
        }

        return japaneseSentenceIds
    }

    private fun populateLinksMap(indicesFile: File,
                               japaneseSentenceIds: Set<Long>,
                               fileReporter: LinesReadReporter,
                               linksMap: HashMap<Long, Long>,
                               onNewLinkParsed: (TatoebaLink) -> Unit = {}) {
        indicesFile.forEachLine { line ->
            fileReporter.assertStillActive()

            val linkLine = parseRelationLine(line)
            if (japaneseSentenceIds.contains(linkLine.sentenceId)) {
                linksMap[linkLine.translationId] = linkLine.sentenceId
            }
            onNewLinkParsed(linkLine)

            fileReporter.reportLine()
        }
    }

    private fun insertEngTranslations(sentencesFile: File,
                                      japaneseSentenceIds: HashSet<Long>,
                                      linksMap: HashMap<Long, Long>,
                                      fileReporter: LinesReadReporter) {
        sentencesFile.forEachLine { line ->
            reporter.assertStillActive()

            val sentenceLine = parseSentenceLine(line)
            val jpnSentenceId = linksMap[sentenceLine.id]
            if (jpnSentenceId != null &&
                sentenceLine.lang == TatoebaSentence.Lang.eng) {
                val newRow = EngTranslationRow(
                    0,
                    jpnSentenceId,
                    sentenceLine.sentence)
                engTranslationBatch.insert(newRow)
                japaneseSentenceIds.remove(jpnSentenceId)
            }
            fileReporter.reportLine()
        }
    }


    private fun populateDBWithJpnSentences(sentencesFile: File): HashSet<Long> {
        if (checkpointManager.reachedCheckpoint(Checkpoint.sentencesReady))
            return dao.getJapaneseSentenceIds().toHashSet()

        val totalSentences = sentencesFile.countLines()
        val japaneseSentenceIds = HashSet<Long>()
        dao.runInTransaction(Runnable {
            reporter.updateProgress(SetupStep.clearingSentencesTable, -1)
            dao.deleteAllSentences()
            insertJpnSentences(
                sentencesFile,
                japaneseSentenceIds,
                newReporter(SetupStep.insertingSentences, totalSentences))
            jpnSentenceBatch.flush()
        })
        checkpointManager.markCheckpoint(
            Checkpoint.sentencesReady, true)
        return japaneseSentenceIds
    }

    private fun populateDBWithJpnIndices(
        indicesFile: File,
        japaneseSentenceIds: HashSet<Long>
    ): HashMap<Long, Long> {
        val linksMap = HashMap<Long, Long>()
        val totalLinks = indicesFile.countLines()
        if (checkpointManager.reachedCheckpoint(Checkpoint.indicesReady)) {
            val lineReporter =
                newReporter(SetupStep.findingTranslations, totalLinks)
            populateLinksMap(
                indicesFile, japaneseSentenceIds, lineReporter, linksMap
            )
            return linksMap
        }

        dao.runInTransaction(Runnable {
            reporter.updateProgress(SetupStep.clearingIndicesTable, -1)
            dao.deleteAllJpnIndices()

            val lineReporter = newReporter(SetupStep.insertingIndices, totalLinks)
            populateLinksMap(
                indicesFile, japaneseSentenceIds, lineReporter, linksMap
            ) { newLink ->
                val newRow = JpnIndicesRow(
                    0,
                    newLink.sentenceId,
                    newLink.japaneseIndices)
                jpnIndicesBatch.insert(newRow)
            }
            jpnIndicesBatch.flush()
        })
        checkpointManager.markCheckpoint(
            Checkpoint.indicesReady, true)
        return linksMap
    }

    private fun populateDBWithEngTranslations(translationsFile: File,
                                              japaneseSentenceIds: HashSet<Long>,
                                              linksMap: HashMap<Long, Long>) {
        if (checkpointManager.reachedCheckpoint(Checkpoint.translationsReady))
            return

        val totalTranslations = translationsFile.countLines()
        dao.runInTransaction(Runnable {
            reporter.updateProgress(SetupStep.clearingTranslationsTable, -1)
            dao.deleteAllTranslations()
            insertEngTranslations(
                translationsFile,
                japaneseSentenceIds,
                linksMap,
                newReporter(SetupStep.insertingTranslations, totalTranslations)
            )
            engTranslationBatch.flush()

            reporter.updateProgress(SetupStep.wrappingUp, -1)
            deleteSentencesWithoutTranslations(japaneseSentenceIds)
        })
        checkpointManager.markCheckpoint(Checkpoint.translationsReady, true)
    }

    suspend fun exec(): Either<Exception, Unit> {
        try {
            val sentencesFile = sentencesDownloader.retrieve(reporter)
            val japaneseSentenceIds =
                populateDBWithJpnSentences(sentencesFile)

            val indicesFile = when (val either = deferredIndicesFile.await()) {
                is Either.Left ->
                    // the deferred download failed so
                    // we return to quit the operation
                    return Either.Left(either.value)
                is Either.Right ->
                    either.value
            }
            val linksMap =
                populateDBWithJpnIndices(indicesFile, japaneseSentenceIds)

            val translationsFile = when (val either = deferredTranslationsFile.await()) {
                is Either.Left ->
                    // the deferred download failed so
                    // we return to quit the operation
                    return Either.Left(either.value)
                is Either.Right ->
                    either.value
            }

            populateDBWithEngTranslations(
                translationsFile,
                japaneseSentenceIds,
                linksMap
            )
            return Either.Right(Unit)
        } catch (ex: Exception) {
            return Either.Left(ex)
        } finally {
            // cancel any deferred downloads that are still active
            if (deferredIndicesFile.isActive)
                deferredIndicesFile.cancel()
            if (deferredTranslationsFile.isActive)
                deferredTranslationsFile.cancel()

            reporter.dismiss()
        }

    }

    private fun newReporter(step: SetupStep, total: Int) =
        LinesReadReporter(reporter, step, total)
}
