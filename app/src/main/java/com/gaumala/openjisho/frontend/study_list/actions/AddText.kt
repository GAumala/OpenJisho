package com.gaumala.openjisho.frontend.study_list.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.common.StudyCard
import com.gaumala.openjisho.frontend.study_list.StudyListSideEffect
import com.gaumala.openjisho.frontend.study_list.StudyListState
import com.gaumala.openjisho.frontend.study_list.LoadedStudyCards

data class AddText(val id: Long,
                   val newText: String)
    : Action<StudyListState, StudyListSideEffect>() {

    override fun update(state: StudyListState)
            : Update<StudyListState, StudyListSideEffect> {
        if (state.cards !is LoadedStudyCards.Ready)
            return Update(state)

        val newStudyItem = StudyCard.Text(id, newText)
        val newList = state.cards.list.plus(newStudyItem)
        val newState = state.copy(cards = LoadedStudyCards.Ready(newList))

        val sideEffect = StudyListSideEffect.Write(newList)

        return Update(newState, sideEffect)
    }
}