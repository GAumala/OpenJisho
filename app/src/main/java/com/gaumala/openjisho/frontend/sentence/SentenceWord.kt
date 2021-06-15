package com.gaumala.openjisho.frontend.sentence

import com.gaumala.openjisho.common.JMdictEntry

sealed class SentenceWord {
    data class JMdict(val entry: JMdictEntry.Summarized): SentenceWord()
    data class Unknown(val text: String): SentenceWord()
}