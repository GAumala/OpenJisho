package com.gaumala.openjisho.frontend.dict

import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.utils.async.MessageThrottler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DictSideEffectRunner(private val scope: CoroutineScope,
                           private val searchBroker: DictSearchBroker,
                           private val searchThrottler: MessageThrottler<DictSearchMsg>)
    : SideEffectRunner<DictState, DictSideEffect> {

    override fun runSideEffect(
        sink: ActionSink<DictState, DictSideEffect>,
        args: DictSideEffect) {
        if (args is DictSideEffect.Search)
            search(sink, args)

        else if (args is DictSideEffect.GetSuggestions)
            getSuggestions(sink, args)
    }

    private fun search(sink: ActionSink<DictState, DictSideEffect>,
                       args: DictSideEffect.Search) {
        val msg = DictSearchMsg(args.params, sink)
        if (args.shouldThrottle)
            searchThrottler.sendMessage(msg)
        else {
            scope.launch(Dispatchers.Main) {
                searchBroker.handleMessage(msg)
            }
        }
    }

    private fun getSuggestions(
        sink: ActionSink<DictState, DictSideEffect>,
        args: DictSideEffect.GetSuggestions
    ) {
        searchBroker.getSuggestionsForLastEntryResults(
            sink, args.queryText, args.lastResults
        )
    }
}