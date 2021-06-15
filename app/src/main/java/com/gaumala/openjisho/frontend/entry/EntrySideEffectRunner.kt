package com.gaumala.openjisho.frontend.entry

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicConverter
import com.gaumala.openjisho.frontend.entry.actions.PostKanji
import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import kotlinx.coroutines.*
import java.util.*

class EntrySideEffectRunner (private val dao: DictQueryDao) : SideEffectRunner<EntryState, EntrySideEffect> {
    private val defaultJob = Job()
    private val defaultScope =
        CoroutineScope(defaultJob + Dispatchers.Main)

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
        defaultScope.launch(Dispatchers.Main) {
            val results = withContext(Dispatchers.IO) {

                val literals = args.kanjiElements.flatMap {
                    val chars = LinkedList<String>()
                    for(i in it.text.indices)
                        chars.add(it.text[i].toString())
                    chars
                }

                dao.lookupKanjidicRowExact(literals).map {
                    KanjidicConverter.fromKanjiRow(it)
                }
            }

            sink.submitAction(PostKanji(results))
        }
    }
}