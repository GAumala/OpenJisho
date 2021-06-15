package com.gaumala.openjisho.frontend.dict

import android.os.Parcelable
import com.gaumala.openjisho.backend.dict.DictCache
import com.gaumala.openjisho.frontend.navigation.MainScreen
import com.gaumala.openjisho.utils.recycler.PaginationStatus

data class DictState(val stateToRestore: DictSavedState? = null,
                     val entryResults: EntryResults,
                     val sentenceResults: SentenceResults,
                     val destination: MainScreen? = null) {

    constructor(): this(
        stateToRestore = null,
        entryResults = EntryResults.Welcome,
        sentenceResults = SentenceResults.Welcome,
        destination = null)

    fun toSavedState(queryText: String,
                     selectedTab: Int,
                     entriesState: Parcelable?,
                     sentencesState: Parcelable?): DictSavedState? {
        if (queryText.isEmpty())
            return null

        val hadEntries = entryResults is EntryResults.Ready
                && entryResults.queryText == queryText
        val hadSentences = sentenceResults is SentenceResults.Ready
                && sentenceResults.queryText == queryText
        val entriesPStatus = if (entryResults is EntryResults.Ready)
                entryResults.pagination else PaginationStatus.complete
        val sentencesPStatus = if (sentenceResults is SentenceResults.Ready)
            sentenceResults.pagination else PaginationStatus.complete
        return DictSavedState(
            queryText = queryText,
            selectedTab = selectedTab,
            entriesPStatus = entriesPStatus,
            sentencesPStatus = sentencesPStatus,
            entriesState = if (hadEntries) entriesState else null,
            sentencesState = if (hadSentences) sentencesState else null
        )
    }

    companion object {
        fun fromSavedState(dictCache: DictCache,
                           savedState: DictSavedState?): DictState {
            if (savedState == null)
                return DictState()

            val queryText = savedState.queryText
            if (queryText.isEmpty())
                return DictState()

            val cachedEntries = dictCache.getCachedEntryResults(queryText)
            val entryResults =
                if (cachedEntries == null)
                    EntryResults.Loading(queryText)
                else {
                    val paginationStatus =
                        if (cachedEntries.size < DictCache.PAGE_SIZE)
                            PaginationStatus.complete
                        else savedState.entriesPStatus
                    EntryResults.Ready(
                        queryText, paginationStatus, cachedEntries)
                }

            val cachedSentences = dictCache.getCachedSentenceResults(queryText)
            val sentenceResults =
                if (cachedSentences == null)
                    SentenceResults.Loading(queryText)
                else {
                    val paginationStatus =
                        if (cachedSentences.size < DictCache.PAGE_SIZE)
                            PaginationStatus.complete
                        else savedState.sentencesPStatus
                    SentenceResults.Ready(
                        queryText, paginationStatus, cachedSentences)
                }

            val stateToRestore = DictSavedState(
                queryText = queryText,
                selectedTab = savedState.selectedTab,
                entriesPStatus = savedState.entriesPStatus,
                sentencesPStatus = savedState.sentencesPStatus,
                entriesState = if (entryResults is EntryResults.Ready)
                    savedState.entriesState else null,
                sentencesState = if (sentenceResults is SentenceResults.Ready)
                    savedState.sentencesState else null
            )

            return DictState(
                stateToRestore = stateToRestore,
                entryResults = entryResults,
                sentenceResults = sentenceResults)
        }
    }
}