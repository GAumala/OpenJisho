package com.gaumala.openjisho.frontend.user_sentence

import com.gaumala.openjisho.common.WordIndex
import com.gaumala.openjisho.frontend.sentence.SentenceWord

data class UserSentenceState(
    val text: String,
    val indices: List<WordIndex>,
    val words: List<SentenceWord>,
) {
    constructor(text: String) : this(text, emptyList(), emptyList())
}
