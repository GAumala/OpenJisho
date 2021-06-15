package com.gaumala.openjisho.frontend.sentence.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.sentence.SentenceSideEffect
import com.gaumala.openjisho.frontend.sentence.SentenceState
import com.gaumala.openjisho.frontend.sentence.SentenceWord

class LoadWords(
    val words: List<SentenceWord>
): Action<SentenceState, SentenceSideEffect>() {

    override fun update(state: SentenceState): Update<SentenceState, SentenceSideEffect> {
        return Update(state.copy(words = words))
    }
}