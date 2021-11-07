package com.gaumala.openjisho.frontend

import com.gaumala.openjisho.frontend.dict.EntryResult

data class QuerySuggestion(
    val queryText: String,
    val results: List<EntryResult>
)