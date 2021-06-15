package com.gaumala.openjisho.frontend.dict

import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.utils.recycler.PaginationStatus
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DictSavedState(val queryText: String,
                          val selectedTab: Int,
                          val entriesPStatus: PaginationStatus,
                          val sentencesPStatus: PaginationStatus,
                          val entriesState: Parcelable?,
                          val sentencesState: Parcelable?): Parcelable {


    fun withNewQuery(newQueryText: String): DictSavedState {
        if (newQueryText == queryText)
            return this

        return copy(
            queryText = newQueryText,
            entriesPStatus = PaginationStatus.canLoadMore,
            sentencesPStatus = PaginationStatus.canLoadMore,
            entriesState = null,
            sentencesState = null)
    }

    companion object {
        fun updateQuery(savedState: DictSavedState?,
                        newQueryText: String): DictSavedState? {
            if (newQueryText.isEmpty())
                return null

            return savedState?.withNewQuery(newQueryText)
                ?: DictSavedState(
                    queryText = newQueryText,
                    selectedTab = 0,
                    entriesPStatus = PaginationStatus.canLoadMore,
                    sentencesPStatus = PaginationStatus.canLoadMore,
                    entriesState = null,
                    sentencesState = null
                )
        }
    }
}