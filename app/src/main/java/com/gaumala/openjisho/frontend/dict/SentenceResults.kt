package com.gaumala.openjisho.frontend.dict

import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.utils.recycler.PaginationStatus

sealed class SentenceResults {
    object Welcome: SentenceResults()
    data class Loading(val queryText: String): SentenceResults()
    data class Ready(val queryText: String,
                     val pagination: PaginationStatus,
                     val results: List<Sentence>): SentenceResults() {

        val nextOffset: Int
            get() = results.size

        val isLoadingMore: Boolean
            get() = pagination == PaginationStatus.isLoadingMore

        val shouldShowLoadingMore: Boolean
            get() = pagination != PaginationStatus.complete

        fun isLoadingMoreWith(queryText: String): Boolean =
            this.queryText == queryText
                    && pagination == PaginationStatus.isLoadingMore

        fun addPage(newPage: List<Sentence>, canLoadMore: Boolean): Ready {
            val newPaginationStatus =
                if (canLoadMore) PaginationStatus.canLoadMore
                else PaginationStatus.complete
            return Ready(queryText, newPaginationStatus, results.plus(newPage))
        }
    }
    class Error(val queryText: String,
                val message: UIText): SentenceResults()
}
