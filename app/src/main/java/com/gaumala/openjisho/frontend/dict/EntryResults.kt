package com.gaumala.openjisho.frontend.dict

import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.utils.recycler.PaginationStatus

sealed class EntryResults {
    object Welcome: EntryResults()
    data class Loading(val queryText: String): EntryResults()
    data class Ready(val queryText: String,
                     val pagination: PaginationStatus,
                     val items: List<EntryResult>): EntryResults() {

        val nextOffset: Int
            get() = items.count { it is EntryResult.JMdict }

        val isLoadingMore: Boolean
            get() = pagination == PaginationStatus.isLoadingMore

        val shouldShowLoadingMore: Boolean
            get() = pagination != PaginationStatus.complete

        fun isLoadingMoreWith(queryText: String): Boolean =
            this.queryText == queryText
                    && pagination == PaginationStatus.isLoadingMore

        fun addPage(newPage: List<EntryResult>, canLoadMore: Boolean): Ready {
            val newPaginationStatus =
                if (canLoadMore) PaginationStatus.canLoadMore
                else PaginationStatus.complete
            return copy(
                pagination = newPaginationStatus,
                items = items.plus(newPage))
        }

    }

    data class Error(
        val queryText: String,
        val message: UIText
    ): EntryResults()

    data class ErrorWithSuggestions(
        val originalQuery: String,
        val suggestedQueries: List<String>
    ): EntryResults()
}