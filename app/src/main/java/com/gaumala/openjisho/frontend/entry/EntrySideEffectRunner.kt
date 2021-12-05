package com.gaumala.openjisho.frontend.entry

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicConverter
import com.gaumala.openjisho.frontend.entry.actions.PostKanji
import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.backend.dict.KanjidicComparator
import kotlinx.coroutines.*
import java.util.*

class EntrySideEffectRunner (
    private val scope: CoroutineScope,
    private val dao: DictQueryDao
): SideEffectRunner<EntryState, EntrySideEffect> {

    override fun runSideEffect(
        sink: ActionSink<EntryState, EntrySideEffect>,
        args: EntrySideEffect
    ) {
        if (args is EntrySideEffect.SearchKanji)
            searchKanji(sink, args)
    }
    private fun searchKanji(
        sink: ActionSink<EntryState, EntrySideEffect>,
        args: EntrySideEffect.SearchKanji
    ) {
        scope.launch(Dispatchers.Main) {
            val results = withContext(Dispatchers.IO) {

                val kanjiText = args.kanjiElements
                    .joinToString(separator = "") { it.text }
                val literals = kanjiText.toCharArray()
                    .map { it.toString() }

                dao.lookupKanjidicRowExact(literals)
                    .sortedWith(KanjidicComparator(kanjiText))
                    .map {
                        KanjidicConverter.fromKanjiRow(it)
                    }
            }

            sink.submitAction(PostKanji(results))
        }
    }
}