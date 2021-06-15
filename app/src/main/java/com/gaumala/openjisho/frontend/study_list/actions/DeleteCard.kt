package com.gaumala.openjisho.frontend.study_list.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.study_list.LoadedStudyCards
import com.gaumala.openjisho.frontend.study_list.StudyListMsg
import com.gaumala.openjisho.frontend.study_list.StudyListSideEffect
import com.gaumala.openjisho.frontend.study_list.StudyListState

class DeleteCard(val targetIndex: Int): Action<StudyListState, StudyListSideEffect>() {
    private val filterOutTarget = { i: Int, _: Any ->
              i != targetIndex
    }

    override fun update(state: StudyListState)
            : Update<StudyListState, StudyListSideEffect> {
        val loadedCards = state.cards as? LoadedStudyCards.Ready
            ?: return Update(state)

        val newList = loadedCards.list
            .filterIndexed(filterOutTarget)

        val newState = state.copy(
            backedUpCards = loadedCards,
            snackbarMsg = StudyListMsg.itemRemoved,
            cards = LoadedStudyCards.Ready(newList))

        val sideEffect = StudyListSideEffect.Write(newList)
        return Update(newState, sideEffect)
    }
}