package com.gaumala.openjisho.frontend.user_sentence.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.user_sentence.UserSentenceSideEffect
import com.gaumala.openjisho.frontend.user_sentence.UserSentenceState

class SetSentence(
    val sentence: String,
) : Action<UserSentenceState, UserSentenceSideEffect>() {

    override fun update(state: UserSentenceState): Update<UserSentenceState, UserSentenceSideEffect> {
        if (sentence == state.text) {
            return Update(state)
        }

        if (sentence.isEmpty()) {
            return Update(UserSentenceState(text = ""))
        }

        val newState = state.copy(text = sentence)
        val sideEffect = UserSentenceSideEffect.LoadWords(sentence)
        return Update(newState, sideEffect)
    }
}