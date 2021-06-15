package com.gaumala.openjisho.frontend.sentence

import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.frontend.sentence.actions.LoadWords
import kotlinx.coroutines.*

class SentenceSideEffectRunner (
    private val scope: CoroutineScope,
    private val dao: DictQueryDao
) : SideEffectRunner<SentenceState, SentenceSideEffect> {
    private val wordSearchEngine = WordSearchEngine(dao)

    override fun runSideEffect(
        sink: ActionSink<SentenceState, SentenceSideEffect>,
        args: SentenceSideEffect
    ) {
        if (args is SentenceSideEffect.LoadIndices)
            loadSentence(sink, args)
    }

    private fun loadSentence(
        sink: ActionSink<SentenceState, SentenceSideEffect>,
        args: SentenceSideEffect.LoadIndices
    ) {
        scope.launch(Dispatchers.Main) {
            val results = withContext(Dispatchers.IO) {
                val row = dao.lookupSentenceWordIndices(args.sentenceId)
                wordSearchEngine.findSentenceWords(row.indices)
            }

            sink.submitAction(LoadWords(results))
        }
    }
}