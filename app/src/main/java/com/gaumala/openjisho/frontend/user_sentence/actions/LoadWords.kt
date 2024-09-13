package com.gaumala.openjisho.frontend.user_sentence.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.common.WordIndex
import com.gaumala.openjisho.frontend.sentence.SentenceWord
import com.gaumala.openjisho.frontend.user_sentence.UserSentenceSideEffect
import com.gaumala.openjisho.frontend.user_sentence.UserSentenceState

class LoadWords(
    val indices: List<WordIndex>,
    val words: List<SentenceWord>
) : Action<UserSentenceState, UserSentenceSideEffect>() {

    override fun update(state: UserSentenceState): Update<UserSentenceState, UserSentenceSideEffect> {
        return Update(state.copy(indices = indices, words = words))
    }
}