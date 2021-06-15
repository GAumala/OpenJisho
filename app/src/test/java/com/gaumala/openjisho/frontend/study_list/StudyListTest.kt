package com.gaumala.openjisho.frontend.study_list

import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.StudyCard
import com.gaumala.openjisho.frontend.study_list.actions.*
import com.gaumala.openjisho.utils.TestDispatcher
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should equal`
import org.junit.Test


class StudyListTest {
    private val item1 = StudyCard.JMdict(1, JMdictEntry(
        entryId = 1,
        kanjiElements = emptyList(),
        readingElements = emptyList(),
        senseElements = emptyList()
    ))
    private val item2 = StudyCard.JMdict(2, JMdictEntry(
        entryId = 2,
        kanjiElements = emptyList(),
        readingElements = emptyList(),
        senseElements = emptyList()
    ))
    private val item3 = StudyCard.JMdict(3, JMdictEntry(
        entryId = 3,
        kanjiElements = emptyList(),
        readingElements = emptyList(),
        senseElements = emptyList()
    ))
    private val item4 = StudyCard.JMdict(4, JMdictEntry(
        entryId = 4,
        kanjiElements = emptyList(),
        readingElements = emptyList(),
        senseElements = emptyList()
    ))

    private fun moveExample(): TestDispatcher<StudyListState, StudyListSideEffect> {
        val initialState = StudyListState(
            name = "testList",
            cards = LoadedStudyCards.Ready(listOf(
                item1, item2, item3, item4))
        )
        val dispatcher =
            TestDispatcher<StudyListState, StudyListSideEffect>(initialState)

        dispatcher.submitAction(MoveCard(3, 0))
        dispatcher.submitAction(MoveCard(1, 3))

        return dispatcher
    }

    @Test
    fun `after moving a card, should update list`() {
        val dispatcher = moveExample()
        val finalState = dispatcher.getCurrentState()

        val expectedCards = listOf(item4, item2, item3, item1)
        finalState.cards  `should equal` LoadedStudyCards.Ready(expectedCards)
    }

    @Test
    fun `after moving a card, should write edited list to storage`() {
        val dispatcher = moveExample()
        val sideEffect = dispatcher.getLastSideEffect()

        val expectedCards = listOf(item4, item2, item3, item1)
        sideEffect `should equal` StudyListSideEffect.Write(expectedCards)
    }

    private fun singleDeleteExample(): TestDispatcher<StudyListState, StudyListSideEffect> {
        val initialState = StudyListState(
            name = "testList",
            cards = LoadedStudyCards.Ready(listOf(
                item1, item2, item3, item4))
        )
        val dispatcher =
            TestDispatcher<StudyListState, StudyListSideEffect>(initialState)
        dispatcher.submitAction(DeleteCard(3))

        return dispatcher
    }

    @Test
    fun `after a single delete, should update list`() {
        val dispatcher = singleDeleteExample()
        val finalState = dispatcher.getCurrentState()

        val expectedCards = listOf(item1, item2, item3)
        finalState.cards  `should equal` LoadedStudyCards.Ready(expectedCards)
    }

    @Test
    fun `after a single delete, should have a snackBar message and a backup ready`() {
        val dispatcher = singleDeleteExample()
        val finalState = dispatcher.getCurrentState()

        val expectedCards = listOf(item1, item2, item3, item4)
        finalState.backedUpCards `should equal` LoadedStudyCards.Ready(expectedCards)
        finalState.snackbarMsg `should equal` StudyListMsg.itemRemoved
    }

    @Test
    fun `after a single delete, should write edited list to storage`() {
        val dispatcher = singleDeleteExample()
        val sideEffect = dispatcher.getLastSideEffect()

        val expectedCards = listOf(item1, item2, item3)
        sideEffect `should equal` StudyListSideEffect.Write(expectedCards)
    }

    @Test
    fun `after a single delete, should be able dismiss the snackBar and commit the edit`() {
        val dispatcher = singleDeleteExample()
        dispatcher.submitAction(DismissSnackbar(false))

        val finalState = dispatcher.getCurrentState()
        val expectedCards = listOf(item1, item2, item3)

        finalState.snackbarMsg.`should be null`()
        finalState.backedUpCards.`should be null`()
        finalState.cards `should equal` LoadedStudyCards.Ready(expectedCards)
    }

    @Test
    fun `after a single delete, should be able dismiss the snackBar and restore backup`() {
        val dispatcher = singleDeleteExample()
        dispatcher.submitAction(DismissSnackbar(true))

        val finalState = dispatcher.getCurrentState()
        val expectedCards = listOf(item1, item2, item3, item4)

        finalState.snackbarMsg.`should be null`()
        finalState.backedUpCards.`should be null`()
        finalState.cards `should equal` LoadedStudyCards.Ready(expectedCards)
    }
}