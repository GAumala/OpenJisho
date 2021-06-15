package com.gaumala.openjisho.frontend.sentence

import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.common.WordIndex

data class SentenceState(
    val sentence: Sentence,
    val words: List<SentenceWord>
) {
    constructor(sentence: Sentence): this(sentence, emptyList())
}
