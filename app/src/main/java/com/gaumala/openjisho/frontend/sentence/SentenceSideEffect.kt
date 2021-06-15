package com.gaumala.openjisho.frontend.sentence

sealed class SentenceSideEffect {
    data class LoadIndices(val sentenceId: Long): SentenceSideEffect()
}
