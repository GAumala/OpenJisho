package com.gaumala.openjisho.frontend.user_sentence

import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.backend.setup.tatoeba.TatoebaIndicesParser
import com.gaumala.openjisho.frontend.dict.WordSearchMsg
import com.gaumala.openjisho.frontend.user_sentence.actions.LoadWords
import com.gaumala.openjisho.utils.async.MessageThrottler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserSentenceSERunner(
    private val searchThrottler: MessageThrottler<WordSearchMsg>
) : SideEffectRunner<UserSentenceState, UserSentenceSideEffect> {
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
        searchThrottler.sendMessage(WordSearchMsg(sink = sink, sentence = args.text))
    }
}