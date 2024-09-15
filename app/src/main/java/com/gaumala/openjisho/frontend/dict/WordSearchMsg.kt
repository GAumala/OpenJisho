package com.gaumala.openjisho.frontend.dict

import com.gaumala.mvi.ActionSink
import com.gaumala.openjisho.frontend.user_sentence.UserSentenceSideEffect
import com.gaumala.openjisho.frontend.user_sentence.UserSentenceState

data class WordSearchMsg(
    val sink: ActionSink<UserSentenceState, UserSentenceSideEffect>,
    val sentence: String
)