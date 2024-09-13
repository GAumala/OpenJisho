package com.gaumala.openjisho.frontend.user_sentence

import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.setup.tatoeba.TatoebaIndicesParser
import com.gaumala.openjisho.frontend.sentence.WordSearchEngine
import com.gaumala.openjisho.frontend.user_sentence.actions.LoadWords
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserSentenceSERunner(
    private val scope: CoroutineScope,
    private val dao: DictQueryDao
) : SideEffectRunner<UserSentenceState, UserSentenceSideEffect> {
    private val wordSearchEngine = WordSearchEngine(dao)

    override fun runSideEffect(
        sink: ActionSink<UserSentenceState, UserSentenceSideEffect>,
        args: UserSentenceSideEffect
    ) {
        if (args is UserSentenceSideEffect.LoadWords)
            loadWords(sink, args)
    }

    private fun loadWords(
        sink: ActionSink<UserSentenceState, UserSentenceSideEffect>,
        args: UserSentenceSideEffect.LoadWords
    ) {
        scope.launch(Dispatchers.Main) {
            val (indices, words) = withContext(Dispatchers.IO) {
                val indices = TatoebaIndicesParser.parseIndices(args.text)
                val words = wordSearchEngine.findSentenceWords(indices)
                Pair(indices, words)
            }

            sink.submitAction(LoadWords(indices, words))
        }
    }
}