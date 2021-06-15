package com.gaumala.openjisho.frontend.history

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gaumala.openjisho.utils.AsyncFileHandler
import java.io.File

class DictHistoryWidget(callerFragment: Fragment) {
    private val fragmentManager = callerFragment.parentFragmentManager
    private val dictHistory =
        createDictHistory(callerFragment)

    fun open() {
        if (fragmentManager.findFragmentByTag(TAG) != null) return
        // if read() returns null, the data is still loading from file
        // so we can't display anything yet.
        dictHistory.read() ?: return

        val fragment = HistoryDialogFragment.create()
        fragment.show(fragmentManager, TAG)
    }

    fun removeAt(position: Int) {
        dictHistory.removeAt(position)
    }
    fun push(queryText: String) {
        dictHistory.push(queryText)
    }

    fun read(): ArrayList<String> =
        dictHistory.read() ?: ArrayList()

    companion object {
        private const val TAG = "historyBottomSheet"
        private fun createDictHistory(f: Fragment): DictHistory {
            val ctx = f.requireContext()
            val scope = f.lifecycleScope

            val filepath = File(ctx.cacheDir, "history").absolutePath
            val fileHandler = AsyncFileHandler.ChannelFileHandler(scope, filepath)
            return DictHistory(fileHandler)
        }
    }
}
