package com.gaumala.openjisho.backend.setup

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.backend.db.DictDatabase
import com.gaumala.openjisho.backend.setup.file.RawResourceFileRetriever
import com.gaumala.openjisho.backend.setup.file.TatoebaFileRetriever
import com.gaumala.openjisho.backend.keyvalue.KeyValueStorage
import com.gaumala.openjisho.backend.keyvalue.SharedPrefsStorage
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.utils.FileUtils
import com.gaumala.openjisho.utils.error.Either
import kotlinx.coroutines.*
import java.io.File
import java.lang.ref.WeakReference

/**
 * Android [[Service]] that runs the setup in the foreground
 * The actual work is performed by [[SetupWorker]], this class just
 * manages dependencies and runs it.
 */
class SetupService : Service() {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(
        serviceJob + Dispatchers.Main)
    private val handler = Handler()
    private val binder by lazy { SetupBinder() }
    private val foregroundManager by lazy {
        ForegroundManager(
            this
        )
    }
    private val onProgressChange: (SetupStep, Int) -> Unit = { step, progress ->
        handler.post {
            binder.publishProgress(step, progress)
            foregroundManager.updateNotification(step, progress)
        }
    }

    private val dictFilesDir by lazy {
        val filesDir = File(cacheDir, "dict_files")
        filesDir.mkdir()
        filesDir
    }

    private val worker: SetupWorker by lazy {
        val appDB = DictDatabase.getInstance(this)
        val checkpointManager = SetupCheckpointManager.Default(this)
        val radkfileRetriever =
            RawResourceFileRetriever.radkfile(resources, dictFilesDir)
        val kanjidicRetriever =
            RawResourceFileRetriever.kanjidic(resources, dictFilesDir)
        val jmdictRetriever =
            RawResourceFileRetriever.jmdict(resources, dictFilesDir)
        val tatoebaSentencesRetriever =
            TatoebaFileRetriever.sentences(dictFilesDir, checkpointManager)
        val tatoebaTranslationsRetriever =
            TatoebaFileRetriever.translations(dictFilesDir, checkpointManager)
        val tatoebaIndicesRetriever =
            TatoebaFileRetriever.indices(dictFilesDir, checkpointManager)
        SetupWorker(
            reportProgressToService = onProgressChange,
            setupDao = appDB.setupDao(),
            checkpointManager = SetupCheckpointManager.Default(this),
            radkfileRetriever = radkfileRetriever,
            kanjidicRetriever = kanjidicRetriever,
            jmdictRetriever = jmdictRetriever,
            tatoebaSentencesRetriever = tatoebaSentencesRetriever,
            tatoebaTranslationsRetriever = tatoebaTranslationsRetriever,
            tatoebaIndicesRetriever = tatoebaIndicesRetriever)
    }


    private fun runSetup() {
        if (isRunning)
            return
        isRunning = true

        val kvStorage = SharedPrefsStorage(this)
        if (kvStorage.getBoolean(KeyValueStorage.Key.dbSetup, false)) {
            // setup is already complete, exit with success
            binder.publishResult(Either.Right(Unit))
            return
        }

        serviceScope.launch(Dispatchers.Main) {

            foregroundManager.start()

            val result: Either<Exception, Unit> = withContext(Dispatchers.IO) {
                val availableSpace = FileUtils.getAvailableSpace()
                if (availableSpace < minRequiredFreeSpace) {
                    val errorMsg = getString(R.string.not_enough_available_space)
                    Either.Left(Exception(errorMsg))
                } else
                    worker.doWork(this)
            }

            isRunning = false
            foregroundManager.stop()
            binder.publishResult(result.map {
                // if success, update key value storage
                kvStorage.putBoolean(KeyValueStorage.Key.dbSetup, true)
                // also clear dict files
                launch(Dispatchers.IO) {
                    val takenSpace = dictFilesDir.listFiles().fold(0L) { totalSize, file -> totalSize + file.length() }
                    Log.d("DictDebug", "total space: $takenSpace")
                    dictFilesDir.deleteRecursively()
                }
                Unit
            })
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runSetup()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.coroutineContext.cancelChildren()
    }

    interface Frontend {
        fun bind(listener: SetupServiceListener)
        fun unbind()
    }

    private inner class SetupBinder : Binder(), Frontend {

        private var listenerRef = WeakReference<SetupServiceListener>(null)
        private var lastResult: Either<Exception, Unit>? = null
        private var lastProgressUpdate = Pair(SetupStep.initializing, 0)

        override fun bind(listener: SetupServiceListener) {
            listenerRef = WeakReference(listener)
            val r = lastResult
            if (r != null)
                publishResult(r)
            else
                publishProgress(lastProgressUpdate.first,
                    lastProgressUpdate.second)
        }

        override fun unbind() {
            listenerRef.clear()
            if (! isRunning)
                this@SetupService.stopSelf()
        }

        fun publishProgress(step: SetupStep, progress: Int) {
            lastProgressUpdate = Pair(step, progress)
            listenerRef.get()?.onProgressChange(step, progress)
        }

        fun publishResult(result: Either<Exception, Unit>) {
            lastResult = result
            listenerRef.get()?.onComplete(resultToUIText(result))
        }

        private fun resultToUIText(result: Either<Exception, Unit>)
                : UIText? {
            return when (result) {
                is Either.Left ->
                    UIText.Literal(result.value.message ?: "Unknown Error")
                is Either.Right -> null
            }
        }
    }

    companion object {
        var isRunning = false
        private set

        private const val minRequiredFreeSpace = 370_000_000L
    }
}