package com.gaumala.openjisho.frontend.dict

import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.utils.recycler.PaginationStatus

sealed class EntryResults {
    object Welcome: EntryResults()
    data class Loading(val queryText: String): EntryResults()
    data class Ready(val queryText: String,
                     val pagination: PaginationStatus,
                     val kanjiCount: Int,
                     val results: List<EntryResult>): EntryResults() {

        constructor(queryText: String,
                    pagination: PaginationStatus,
                    results: List<EntryResult>)
                : this(queryText,
                       pagination,
                       results.count { it is EntryResult.Kanjidic },
                       results)
        val nextOffset: Int
            get() = results.size - kanjiCount

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
                results = results.plus(newPage))
        }

    }
    data class Error(val queryText: String,
                val message: UIText): EntryResults()
}