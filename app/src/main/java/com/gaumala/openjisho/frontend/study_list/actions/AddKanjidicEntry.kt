package com.gaumala.openjisho.frontend.study_list.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.common.StudyCard
import com.gaumala.openjisho.frontend.study_list.LoadedStudyCards
import com.gaumala.openjisho.frontend.study_list.StudyListSideEffect
import com.gaumala.openjisho.frontend.study_list.StudyListState

data class AddKanjidicEntry(val id: Long,
                            val newEntry: KanjidicEntry)
    : Action<StudyListState, StudyListSideEffect>() {

    override fun update(state: StudyListState)
            : Update<StudyListState, StudyListSideEffect> {
        if (state.cards !is LoadedStudyCards.Ready)
            return Update(state)

        val newStudyItem = StudyCard.Kanjidic(id, newEntry)
        val newList = state.cards.list.plus(newStudyItem)
        val newState = state.copy(cards = LoadedStudyCards.Ready(newList))

        val sideEffect = StudyListSideEffect.Write(newList)

        return Update(newState, sideEffect)
    }
}