package com.gaumala.openjisho.frontend.dict

data class DictSearchParams(val queryText: String,
                            val lookupSentences: Boolean,
                            val offset: Int) {
    companion object {
        fun createStartupSearchParams(initialState: DictState, isShowingEntries: Boolean): DictSearchParams? {
            val entryResults = initialState.entryResults
            val sentenceResults = initialState.sentenceResults

            return if (isShowingEntries) {
                if (entryResults is EntryResults.Loading)
                    DictSearchParams(
                        queryText = entryResults.queryText,
                        lookupSentences = false,
                        offset = 0
                    )
                else if (entryResults is EntryResults.Ready
                    && entryResults.isLoadingMore
                )
                    DictSearchParams(
                        queryText = entryResults.queryText,
                        lookupSentences = false,
                        offset = entryResults.nextOffset
                    )
                else null
            } else {
                if (sentenceResults is SentenceResults.Loading)
                    DictSearchParams(
                        queryText = sentenceResults.queryText,
                        lookupSentences = true,
                        offset = 0
                    )
                else if (sentenceResults is SentenceResults.Ready
                    && sentenceResults.isLoadingMore
                )
                    DictSearchParams(
                        queryText = sentenceResults.queryText,
                        lookupSentences = true,
                        offset = sentenceResults.nextOffset
                    )
                else null
            }
        }
    }
}