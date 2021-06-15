package com.gaumala.openjisho.frontend.study_list.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.frontend.study_list.LoadedStudyCards
import com.gaumala.openjisho.frontend.study_list.StudyListSideEffect
import com.gaumala.openjisho.frontend.study_list.StudyListState

class DismissSnackbar(val restoreBackup: Boolean)
    : Action<StudyListState, StudyListSideEffect>() {

    override fun update(state: StudyListState)
            : Update<StudyListState, StudyListSideEffect> {
        val loadedCards =
            state.cards as? LoadedStudyCards.Ready ?: return Update(state)

        val backedUpCards = state.backedUpCards ?: return Update(
            state.copy(snackbarMsg = null))

        val newCards = if (restoreBackup) backedUpCards else loadedCards
        val newState = state.copy(
            snackbarMsg = null,
            backedUpCards = null,
            cards = newCards)

        val sideEffect = if (restoreBackup)
            StudyListSideEffect.Write(newCards.list)
        else
            null

        return Update(newState, sideEffect)
    }
}