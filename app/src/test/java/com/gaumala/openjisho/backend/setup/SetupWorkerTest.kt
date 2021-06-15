package com.gaumala.openjisho.backend.setup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gaumala.openjisho.backend.db.*
import com.gaumala.openjisho.backend.setup.file.DictFileSamples
import com.gaumala.openjisho.backend.setup.file.FileRetriever
import com.gaumala.openjisho.backend.setup.file.MockedFileRetriever
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.utils.TestCoroutineRule
import com.gaumala.openjisho.utils.error.Either
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SetupWorkerTest {
    @get:Rule
    var folder = TemporaryFolder()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    @get:Rule
    var timeout = CoroutinesTimeout.seconds(12)

    private fun mockSetupDao(): SetupDao {
        val setupDao = mockk<SetupDao>(relaxed = true)
        val trxSlot = slot<Runnable>()
        every {
            setupDao.runInTransaction(capture(trxSlot))
        } answers {
            trxSlot.captured.run()
        }
        return setupDao
    }

    // The default delays try to be proportionate to the file
    // transfer length in production
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun runSetupWorker(
        scope: CoroutineScope,
        checkpointManager: MockedCheckpointManager = MockedCheckpointManager(),
        setupDao: SetupDao = mockSetupDao(),
        reportProgress: (SetupStep, Int) -> Unit = mockk(relaxed = true),
        radkfileRetriever: FileRetriever =
            MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 1000L,
                fileCharset = "EUC-JP",
                contents = DictFileSamples.radkfile,
                setupStep = SetupStep.loadingRadkfile
            ),
        kanjidicRetriever: FileRetriever =
            MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 1500L,
                contents = DictFileSamples.kanjidic,
                setupStep = SetupStep.loadingKanjidic
            ),
        jmdictRetriever: FileRetriever =
            MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 3000L,
                contents = DictFileSamples.jmdict,
                setupStep = SetupStep.loadingJMdict
            ),
        tatoebaSentencesRetriever: FileRetriever =
            MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 5000L,
                contents = DictFileSamples.tatoebaSentences,
                setupStep = SetupStep.downloadingTatoebaSentences
            ),
        tatoebaIndicesRetriever: FileRetriever =
            MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 4000L,
                contents = DictFileSamples.tatoebaIndices,
                setupStep = SetupStep.downloadingTatoebaIndices
            ),
        tatoebaTranslationsRetriever: FileRetriever =
            MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 8000L,
                contents = DictFileSamples.tatoebaTranslations,
                setupStep = SetupStep.downloadingTatoebaTranslations
        )
    ): Either<Exception, Unit> {
        val setupWorker = SetupWorker(
            reportProgressToService = reportProgress,
            checkpointManager = checkpointManager,
            jmdictRetriever = jmdictRetriever,
            kanjidicRetriever = kanjidicRetriever,
            radkfileRetriever = radkfileRetriever,
            tatoebaSentencesRetriever = tatoebaSentencesRetriever,
            tatoebaTranslationsRetriever = tatoebaTranslationsRetriever,
            tatoebaIndicesRetriever = tatoebaIndicesRetriever,
            setupDao = setupDao
        )
        val result = setupWorker.doWork(scope)
        testCoroutineRule.dispatcher.advanceTimeBy(12000L)

        return result
    }

    @Test
    fun `should mark all relevant checkpoints and clear them at the end`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()

            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager
            )

            // should succeed
            result `should be instance of` Either.Right::class.java

            // should have marked at some point all of these checkpoints
            checkpointManager.marks `should equal` listOf(
                Pair(Checkpoint.radkfileReady, true),
                Pair(Checkpoint.kanjidicReady, true),
                Pair(Checkpoint.jmdictReady, true),
                Pair(Checkpoint.sentencesReady, true),
                Pair(Checkpoint.indicesReady, true),
                Pair(Checkpoint.translationsReady, true)
            )

            // should have zero active checkpoints
            Checkpoint.values().forEach {
                checkpointManager.reachedCheckpoint(it) `should be` false
            }
        }
    }

    @Test
    fun `should store radkfile entries`() {
        runBlocking {
            val setupDao = mockSetupDao()

            // The batches passed to the dao are mutable and get
            // cleared at the end of the transaction, so we
            // have to capture the rows in another list as soon as the
            // method is called.
            val insertions = mutableListOf<RadicalRow>()
            val rowsSlot = slot<List<RadicalRow>>()
            every {
                setupDao.insertRadicalEntries(capture(rowsSlot))
            } answers {
                insertions.addAll(rowsSlot.captured)
            }

            val result = runSetupWorker(
                scope = this,
                setupDao = setupDao
            )

            // should succeed
            result `should be instance of` Either.Right::class.java

            insertions.size `should be equal to` 90
            insertions `should contain all` listOf(
                RadicalRow(rowid=0, radical="力", kanji="甥"),
                RadicalRow(rowid=0, radical="力", kanji="勉"),
                RadicalRow(rowid=0, radical="力", kanji="黝")
            )
        }
    }

    @Test
    fun `should store kanjidic entries`() {
        runBlocking {
            val setupDao = mockSetupDao()

            // The batches passed to the dao are mutable and get
            // cleared at the end of the transaction, so we
            // have to capture the rows in another list as soon as the
            // method is called.
            val insertions = mutableListOf<KanjidicRow>()
            val rowsSlot = slot<List<KanjidicRow>>()
            every {
                setupDao.insertKanjiEntries(capture(rowsSlot))
            } answers {
                insertions.addAll(rowsSlot.captured)
            }

            val result = runSetupWorker(
                scope = this,
                setupDao = setupDao
            )

            // should succeed
            result `should be instance of` Either.Right::class.java

            insertions `should equal` listOf(
                KanjidicRow(
                    literal = "握",
                    strokes = 12,
                    entryJson = "{\"kunReadings\":[\"にぎ.る\"],\"grade\":8," +
                            "\"jlpt\":1,\"onReadings\":[\"アク\"],\"meanings\"" +
                            ":[\"grip\",\"hold\",\"mould sushi\",\"bribe\"]}")
            )
        }
    }

    @Test
    fun `should store jmdict entries`() {
        runBlocking {
            val setupDao = mockSetupDao()

            // The batches passed to the dao are mutable and get
            // cleared at the end of the transaction, so we
            // have to capture the rows in another list as soon as the
            // method is called.
            val insertions = mutableListOf<JMdictRow>()
            val rowsSlot = slot<List<JMdictRow>>()
            every {
                setupDao.insertEntries(capture(rowsSlot))
            } answers {
                insertions.addAll(rowsSlot.captured)
            }

            val result = runSetupWorker(
                scope = this,
                setupDao = setupDao
            )

            // should succeed
            result `should be instance of` Either.Right::class.java

            insertions `should equal` listOf(
                JMdictRow(
                    id = 1499320,
                    entryJson = "{\"kanji\":[{\"text\":\"部屋\",\"tags\":" +
                            "[\"nf02\"]}],\"reading\":[{\"text\":\"へや\"," +
                            "\"tags\":[\"nf02\"]}],\"sense\":[{\"glossItems\"" +
                            ":[\"room\"],\"glossTags\":[\"n\"]},{\"glossItems\"" +
                            ":[\"stable\"],\"glossTags\":[\"sumo\",\"abbr\"]}]}"
                )
            )
        }
    }

    @Test
    fun `should store tatoeba sentences`() {
        runBlocking {
            val setupDao = mockSetupDao()

            // The batches passed to the dao are mutable and get
            // cleared at the end of the transaction, so we
            // have to capture the rows in another list as soon as the
            // method is called.
            val insertions = mutableListOf<JpnSentenceRow>()
            val rowsSlot = slot<List<JpnSentenceRow>>()
            every {
                setupDao.insertJpnSentences(capture(rowsSlot))
            } answers {
                insertions.addAll(rowsSlot.captured)
            }

            val result = runSetupWorker(
                scope = this,
                setupDao = setupDao
            )

            // should succeed
            result `should be instance of` Either.Right::class.java

            insertions `should equal` listOf(
                JpnSentenceRow(
                    id = 1001,
                    japanese = "こんにちは"
                ),
                JpnSentenceRow(
                    id = 1002,
                    japanese = "こんばんは"
                )
            )
        }
    }

    @Test
    fun `should store tatoeba indices`() {
        runBlocking {
            val setupDao = mockSetupDao()

            // The batches passed to the dao are mutable and get
            // cleared at the end of the transaction, so we
            // have to capture the rows in another list as soon as the
            // method is called.
            val insertions = mutableListOf<JpnIndicesRow>()
            val rowsSlot = slot<List<JpnIndicesRow>>()
            every {
                setupDao.insertJpnIndices(capture(rowsSlot))
            } answers {
                insertions.addAll(rowsSlot.captured)
            }

            val result = runSetupWorker(
                scope = this,
                setupDao = setupDao
            )

            // should succeed
            result `should be instance of` Either.Right::class.java

            insertions `should equal` listOf(
                JpnIndicesRow(
                    rowid = 0,
                    japaneseId = 1001,
                    indices = "こんにちは"
                ),
                JpnIndicesRow(
                    rowid = 0,
                    japaneseId = 1002,
                    indices = "こんばんは"
                )
            )
        }
    }

    @Test
    fun `should store tatoeba translations`() {
        runBlocking {
            val setupDao = mockSetupDao()

            // The batches passed to the dao are mutable and get
            // cleared at the end of the transaction, so we
            // have to capture the rows in another list as soon as the
            // method is called.
            val insertions = mutableListOf<EngTranslationRow>()
            val rowsSlot = slot<List<EngTranslationRow>>()
            every {
                setupDao.insertEngTranslations(capture(rowsSlot))
            } answers {
                insertions.addAll(rowsSlot.captured)
            }

            val result = runSetupWorker(
                scope = this,
                setupDao = setupDao
            )

            // should succeed
            result `should be instance of` Either.Right::class.java

            insertions `should equal` listOf(
                EngTranslationRow(
                    rowid = 0,
                    japaneseId = 1001,
                    english = "Good morning"
                ),
                EngTranslationRow(
                    rowid = 0,
                    japaneseId = 1002,
                    english = "Good evening"
                )
            )
        }
    }

    @Test
    fun `should report progress of every task until they all complete`() {
        runBlocking {
            val reportProgress =
                mockk<(SetupStep, Int) -> Unit>(relaxed = true)

            val result = runSetupWorker(
                scope = this,
                reportProgress = reportProgress
            )

            // should succeed
            result `should be instance of` Either.Right::class.java

            verifyOrder {
                reportProgress(SetupStep.loadingRadkfile, -1)
                reportProgress(SetupStep.clearingRadicalsTable, -1)
                reportProgress(SetupStep.loadingKanjidic, -1)
                reportProgress(SetupStep.clearingKanjiTable, -1)
                reportProgress(SetupStep.loadingJMdict, -1)
                reportProgress(SetupStep.clearingJMdictTable, -1)
                reportProgress(SetupStep.downloadingTatoebaIndices, 1)
                reportProgress(SetupStep.downloadingTatoebaTranslations, 1)
                reportProgress(SetupStep.insertingIndices, 100)
                reportProgress(SetupStep.clearingTranslationsTable, -1)
            }
        }
    }

    @Test
    fun `should mark other tasks as completed if translations download fails`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()
            val translationsRetriever = MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 8000L,
                errorMessage = "Network Error"
            )
            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager,
                tatoebaTranslationsRetriever = translationsRetriever
            )

            // should fail
            result `should be instance of` Either.Left::class.java

            checkpointManager.shouldHaveReached(Checkpoint.radkfileReady)
            checkpointManager.shouldHaveReached(Checkpoint.kanjidicReady)
            checkpointManager.shouldHaveReached(Checkpoint.jmdictReady)
            checkpointManager.shouldHaveReached(Checkpoint.sentencesReady)
            checkpointManager.shouldHaveReached(Checkpoint.indicesReady)

            checkpointManager.shouldHaveNotReached(Checkpoint.translationsReady)
        }
    }

    @Test
    fun `should only insert translations if retrying after a failed translations download`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()
            val setupDao = mockSetupDao()
            every {
                // return ids of already inserted sentences
                setupDao.getJapaneseSentenceIds()
            } returns listOf(1001, 1002)

            // lets pretend everything else has already completed
            checkpointManager.markCheckpoint(Checkpoint.radkfileReady, true)
            checkpointManager.markCheckpoint(Checkpoint.kanjidicReady, true)
            checkpointManager.markCheckpoint(Checkpoint.jmdictReady, true)
            checkpointManager.markCheckpoint(Checkpoint.sentencesReady, true)
            checkpointManager.markCheckpoint(Checkpoint.indicesReady, true)

            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager,
                setupDao = setupDao
            )

            // should fail
            result `should be instance of` Either.Right::class.java

            // should not touch other tables
            verify(inverse = true) { setupDao.deleteAllRadicals() }
            verify(inverse = true) { setupDao.deleteAllKanji() }
            verify(inverse = true) { setupDao.deleteAllEntries() }
            verify(inverse = true) { setupDao.deleteAllSentences() }
            verify(inverse = true) { setupDao.deleteAllJpnIndices() }
            verify(inverse = true) { setupDao.insertRadicalEntries(any()) }
            verify(inverse = true) { setupDao.insertKanjiEntries(any()) }
            verify(inverse = true) { setupDao.insertEntries(any()) }
            verify(inverse = true) { setupDao.insertJpnSentences(any()) }
            verify(inverse = true) { setupDao.insertJpnIndices(any()) }

            // should only insert translations
            verifyOrder {
                setupDao.deleteAllTranslations()
                setupDao.insertEngTranslations(any())
            }
        }
    }

    @Test
    fun `should mark other tasks as completed if indices download fails`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()
            val indicesRetriever = MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 4000L,
                errorMessage = "Network Error"
            )
            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager,
                tatoebaIndicesRetriever = indicesRetriever
            )

            // should fail
            result `should be instance of` Either.Left::class.java

            checkpointManager.shouldHaveReached(Checkpoint.radkfileReady)
            checkpointManager.shouldHaveReached(Checkpoint.kanjidicReady)
            checkpointManager.shouldHaveReached(Checkpoint.jmdictReady)
            checkpointManager.shouldHaveReached(Checkpoint.sentencesReady)

            checkpointManager.shouldHaveNotReached(Checkpoint.indicesReady)
            checkpointManager.shouldHaveNotReached(Checkpoint.translationsReady)
        }
    }

    @Test
    fun `should mark other tasks as completed if sentences download fails`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()
            val sentencesRetriever = MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 5000L,
                errorMessage = "Network Error"
            )
            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager,
                tatoebaSentencesRetriever = sentencesRetriever
            )

            // should fail
            result `should be instance of` Either.Left::class.java

            checkpointManager.shouldHaveReached(Checkpoint.radkfileReady)
            checkpointManager.shouldHaveReached(Checkpoint.kanjidicReady)
            checkpointManager.shouldHaveReached(Checkpoint.jmdictReady)

            checkpointManager.shouldHaveNotReached(Checkpoint.sentencesReady)
            checkpointManager.shouldHaveNotReached(Checkpoint.indicesReady)
            checkpointManager.shouldHaveNotReached(Checkpoint.translationsReady)
        }
    }

    @Test
    fun `should only insert tatoeba rows if retrying after a failed sentences download`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()
            val setupDao = mockSetupDao()

            // lets pretend everything else has already completed
            checkpointManager.markCheckpoint(Checkpoint.radkfileReady, true)
            checkpointManager.markCheckpoint(Checkpoint.kanjidicReady, true)
            checkpointManager.markCheckpoint(Checkpoint.jmdictReady, true)

            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager,
                setupDao = setupDao
            )

            // should fail
            result `should be instance of` Either.Right::class.java

            // should not touch other tables
            verify(inverse = true) { setupDao.deleteAllRadicals() }
            verify(inverse = true) { setupDao.deleteAllKanji() }
            verify(inverse = true) { setupDao.deleteAllEntries() }
            verify(inverse = true) { setupDao.insertRadicalEntries(any()) }
            verify(inverse = true) { setupDao.insertKanjiEntries(any()) }
            verify(inverse = true) { setupDao.insertEntries(any()) }

            // should only insert translations
            verifyOrder {
                setupDao.deleteAllSentences()
                setupDao.insertJpnSentences(any())
                setupDao.deleteAllJpnIndices()
                setupDao.insertJpnIndices(any())
                setupDao.deleteAllTranslations()
                setupDao.insertEngTranslations(any())
            }
        }
    }

    @Test
    fun `should mark other tasks as completed if loading radkfile fails`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()
            val radkfileRetriever = MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 1000L,
                errorMessage = "Network Error"
            )
            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager,
                radkfileRetriever = radkfileRetriever
            )

            // should fail
            result `should be instance of` Either.Left::class.java

            checkpointManager.shouldHaveNotReached(Checkpoint.radkfileReady)

            checkpointManager.shouldHaveReached(Checkpoint.kanjidicReady)
            checkpointManager.shouldHaveReached(Checkpoint.jmdictReady)
            checkpointManager.shouldHaveReached(Checkpoint.sentencesReady)
            checkpointManager.shouldHaveReached(Checkpoint.indicesReady)
            checkpointManager.shouldHaveReached(Checkpoint.translationsReady)
        }
    }

    @Test
    fun `should be able to handle a tatoeba sentence parsing error`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()

            val corruptedContents =
                DictFileSamples.tatoebaSentences.replace('\t', ' ')
            val sentencesRetriever = MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 5000L,
                contents = corruptedContents
            )

            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager,
                tatoebaSentencesRetriever = sentencesRetriever
            )

            // should fail
            result `should be instance of` Either.Left::class.java

            checkpointManager.shouldHaveReached(Checkpoint.radkfileReady)
            checkpointManager.shouldHaveReached(Checkpoint.kanjidicReady)
            checkpointManager.shouldHaveReached(Checkpoint.jmdictReady)

            checkpointManager.shouldHaveNotReached(Checkpoint.sentencesReady)
            checkpointManager.shouldHaveNotReached(Checkpoint.indicesReady)
            checkpointManager.shouldHaveNotReached(Checkpoint.translationsReady)
        }
    }

    @Test
    fun `should be able to handle a tatoeba translations parsing error`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()

            val corruptedContents =
                DictFileSamples.tatoebaTranslations.replace('\t', ' ')
            val translationsRetriever = MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 5000L,
                contents = corruptedContents
            )

            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager,
                tatoebaTranslationsRetriever = translationsRetriever
            )

            // should fail
            result `should be instance of` Either.Left::class.java

            checkpointManager.shouldHaveReached(Checkpoint.radkfileReady)
            checkpointManager.shouldHaveReached(Checkpoint.kanjidicReady)
            checkpointManager.shouldHaveReached(Checkpoint.jmdictReady)
            checkpointManager.shouldHaveReached(Checkpoint.sentencesReady)
            checkpointManager.shouldHaveReached(Checkpoint.indicesReady)

            checkpointManager.shouldHaveNotReached(Checkpoint.translationsReady)
        }
    }

    @Test
    fun `should be able to handle a tatoeba indices parsing error`() {
        runBlocking {
            val checkpointManager = MockedCheckpointManager()

            val corruptedContents =
                DictFileSamples.tatoebaIndices.replace('\t', ' ')
            val indicesRetriever = MockedFileRetriever(
                targetFile = folder.newFile(),
                loadTime = 5000L,
                contents = corruptedContents
            )

            val result = runSetupWorker(
                scope = this,
                checkpointManager = checkpointManager,
                tatoebaIndicesRetriever = indicesRetriever
            )

            // should fail
            result `should be instance of` Either.Left::class.java

            checkpointManager.shouldHaveReached(Checkpoint.radkfileReady)
            checkpointManager.shouldHaveReached(Checkpoint.kanjidicReady)
            checkpointManager.shouldHaveReached(Checkpoint.jmdictReady)
            checkpointManager.shouldHaveReached(Checkpoint.sentencesReady)

            checkpointManager.shouldHaveNotReached(Checkpoint.indicesReady)
            checkpointManager.shouldHaveNotReached(Checkpoint.translationsReady)
        }
    }
}