package com.gaumala.openjisho.frontend.dict

sealed class DictSideEffect {
    data class Search(
        val params: DictSearchParams,
        val shouldThrottle: Boolean
    ) : DictSideEffect()
}