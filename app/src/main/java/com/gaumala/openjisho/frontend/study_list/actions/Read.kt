package com.gaumala.openjisho.frontend.study_list.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.common.StudyCard
import com.gaumala.openjisho.frontend.study_list.StudyListSideEffect
import com.gaumala.openjisho.frontend.study_list.StudyListState
import com.gaumala.openjisho.frontend.study_list.LoadedStudyCards

data class Read(val items: List<StudyCard>?)
    : Action<StudyListState, StudyListSideEffect>() {

    override fun update(state: StudyListState)
            : Update<StudyListState, StudyListSideEffect> {
        if (state.cards !is LoadedStudyCards.Loading)
            return Update(state)

        val newItems =
            if (items == null)
                LoadedStudyCards.Ready(emptyList())
                // LoadedStudyCards.Error(
                //    UIText.Resource(R.string.failed_to_load_list))
            else
                LoadedStudyCards.Ready(items)

        val newState = state.copy(cards = newItems)
        return Update(newState)
    }

}