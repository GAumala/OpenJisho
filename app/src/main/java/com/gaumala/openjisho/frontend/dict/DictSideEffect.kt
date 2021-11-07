package com.gaumala.openjisho.frontend.dict

sealed class DictSideEffect {
    data class Search(
        val params: DictSearchParams,
        val shouldThrottle: Boolean
    ) : DictSideEffect()

    data class GetSuggestions(
        val queryText: String,
        val lastResults: List<EntryResult.JMdict>
    ): DictSideEffect()
}