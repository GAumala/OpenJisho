package com.gaumala.openjisho.frontend.user_sentence

sealed class UserSentenceSideEffect {
    data class LoadWords(val text: String) : UserSentenceSideEffect()
}