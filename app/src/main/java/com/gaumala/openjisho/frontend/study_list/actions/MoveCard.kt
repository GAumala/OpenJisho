package com.gaumala.openjisho.frontend.study_list.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.study_list.LoadedStudyCards
import com.gaumala.openjisho.frontend.study_list.StudyListSideEffect
import com.gaumala.openjisho.frontend.study_list.StudyListState

class MoveCard(val src: Int, val dst: Int): Action<StudyListState, StudyListSideEffect>() {

    override fun update(state: StudyListState)
            : Update<StudyListState, StudyListSideEffect> {
        val loadedCards =
            state.cards as? LoadedStudyCards.Ready ?: return Update(state)

        val arrayList = ArrayList(loadedCards.list)
        val rearranged = arrayList.removeAt(src)
        arrayList.add(dst, rearranged)

        val newState = state.copy(
            cards = LoadedStudyCards.Ready(
                list = arrayList))
        val sideEffect = StudyListSideEffect.Write(arrayList)

        return Update(newState, sideEffect)
    }
}