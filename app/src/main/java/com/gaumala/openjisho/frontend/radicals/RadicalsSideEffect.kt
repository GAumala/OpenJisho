package com.gaumala.openjisho.frontend.radicals

sealed class RadicalsSideEffect {
    data class SearchKanji(val radicals: List<RadicalIndex>,
                           val combination: List<String>): RadicalsSideEffect()
}
