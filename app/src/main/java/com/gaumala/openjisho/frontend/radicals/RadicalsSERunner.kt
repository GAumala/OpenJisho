package com.gaumala.openjisho.frontend.radicals

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.frontend.radicals.actions.PostResults
import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.frontend.radicals.RadicalsQueries.searchKanjiWithRadicalCombination
import kotlinx.coroutines.*

class RadicalsSERunner(private val dao: DictQueryDao)
    : SideEffectRunner<RadicalsState, RadicalsSideEffect> {

    private val defaultJob = Job()
    private val defaultScope =
        CoroutineScope(defaultJob + Dispatchers.Main)

    override fun runSideEffect(
        sink: ActionSink<RadicalsState, RadicalsSideEffect>,
        args: RadicalsSideEffect) {
        if (args is RadicalsSideEffect.SearchKanji)
            searchKanji(sink, args)
    }


    private fun searchKanji(
        sink: ActionSink<RadicalsState, RadicalsSideEffect>,
        args: RadicalsSideEffect.SearchKanji) {
        defaultScope.launch(Dispatchers.Main) {
            val (results, radicals) = withContext(Dispatchers.IO) {
                searchKanjiWithRadicalCombination(
                    dao, args.combination, args.radicals)
            }
            sink.submitAction(PostResults(results, radicals))
        }
    }
}