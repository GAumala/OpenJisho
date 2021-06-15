package com.gaumala.openjisho.frontend.dict

import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.utils.async.MessageThrottler

class DictSideEffectRunner(private val searchBroker: DictSearchBroker,
                           private val searchThrottler: MessageThrottler<DictSearchMsg>)
    : SideEffectRunner<DictState, DictSideEffect> {

    override fun runSideEffect(
        sink: ActionSink<DictState, DictSideEffect>,
        args: DictSideEffect) {
        if (args is DictSideEffect.Search)
            search(sink, args)
    }

    private fun search(sink: ActionSink<DictState, DictSideEffect>,
                       args: DictSideEffect.Search) {
        val msg = DictSearchMsg(args.params, sink)
        if (args.shouldThrottle)
            searchThrottler.sendMessage(msg)
        else {
            searchBroker.handleMessage(msg)
        }
    }
}