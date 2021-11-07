package com.gaumala.openjisho.utils.error

import com.gaumala.openjisho.frontend.QuerySuggestion
import java.lang.Exception

class BetterQueriesException(
    val suggestions: List<QuerySuggestion>
): Exception()
