package com.gaumala.openjisho.frontend.entry

import com.gaumala.openjisho.common.JMdictEntry

sealed class EntrySideEffect {
    data class SearchKanji(val kanjiElements: List<JMdictEntry.Element>): EntrySideEffect()
}
