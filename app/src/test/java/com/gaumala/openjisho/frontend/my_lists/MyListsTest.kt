package com.gaumala.openjisho.frontend.my_lists

import com.gaumala.openjisho.frontend.my_lists.actions.*
import com.gaumala.openjisho.utils.TestDispatcher
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should equal`
import org.junit.Test

class MyListsTest {
    private val item1 = ListMetadata("List #1")
    private val item2 = ListMetadata("List #2")
    private val item3 = ListMetadata("List #3")
    private val item4 = ListMetadata("List #4")

    private fun bulkDeleteExample(): TestDispatcher<MyListsState, MyListsSideEffect> {
        val initialState = MyListsState(
            lists = LoadedLists.Ready(
                listOf(
                    item1, item2, item3, item4
                )
            )
        )
        val dispatcher =
            TestDispatcher<MyListsState, MyListsSideEffect>(initialState)

        dispatcher.submitAction(StartSelection("List #2"))
        dispatcher.submitAction(ToggleSelection("List #3"))
        dispatcher.submitAction(DeleteSelection())

        return dispatcher
    }

    @Test
    fun `after a bulk delete, should exit multi select with new list`() {
        val dispatcher = bulkDeleteExample()
        val finalState = dispatcher.getCurrentState()

        val expectedLists = listOf(item1, item4)
        finalState.lists  `should equal` LoadedLists.Ready(expectedLists)
    }

    @Test
    fun `after a bulk delete, should have a snackBar message`() {
        val dispatcher = bulkDeleteExample()
        val finalState = dispatcher.getCurrentState()

        finalState.snackbarMsg `should equal` MyListsMsg.selectionDeleted
    }

    @Test
    fun `after a bulk delete, should have pending deletions`() {
        val dispatcher = bulkDeleteExample()
        val finalState = dispatcher.getCurrentState()

        val expectedDeletions = listOf(
            DeletedMetadata(item2, 1),
            DeletedMetadata(item3, 2))

        finalState.pendingDeletions `should equal` expectedDeletions
    }

    @Test
    fun `after a bulk delete, DismissSnackbar can emit Delete side effect`() {
        val dispatcher = bulkDeleteExample()

        dispatcher.getAllSideEffects().`should be empty`()

        dispatcher.submitAction(
            DismissSnackbar(false))

        val sideEffect = dispatcher.getLastSideEffect()

        val expectedDeletions = listOf(item2.name, item3.name)
        sideEffect `should equal` MyListsSideEffect.Delete(expectedDeletions)
    }

    @Test
    fun `after a bulk delete, DismissSnackbar can flush pending deletions`() {
        val dispatcher = bulkDeleteExample()
        dispatcher.submitAction(DismissSnackbar(false))
        val finalState = dispatcher.getCurrentState()

        val expectedLists = listOf(item1, item4)
        finalState.lists  `should equal` LoadedLists.Ready(expectedLists)
        finalState.snackbarMsg.`should be null`()
        finalState.pendingDeletions.`should be empty`()
    }

    @Test
    fun `after a bulk delete, DismissSnackbar can undo deletions`() {
        val dispatcher = bulkDeleteExample()
        dispatcher.submitAction(DismissSnackbar(true))
        val finalState = dispatcher.getCurrentState()

        val expectedLists = listOf(item1, item2, item3, item4)
        finalState.lists  `should equal` LoadedLists.Ready(expectedLists)
        finalState.snackbarMsg.`should be null`()
        finalState.pendingDeletions.`should be empty`()
    }

    @Test
    fun `after a bulk delete, DismissSnackbar can prevent Delete side effect`() {
        val dispatcher = bulkDeleteExample()
        dispatcher.submitAction(DismissSnackbar(true))

        dispatcher.getAllSideEffects().`should be empty`()
    }

    private fun singleDeleteExample(): TestDispatcher<MyListsState, MyListsSideEffect> {
        val initialState = MyListsState(
            lists = LoadedLists.Ready(
                listOf(
                    item1, item2, item3, item4
                )
            )
        )
        val dispatcher =
            TestDispatcher<MyListsState, MyListsSideEffect>(initialState)

        dispatcher.submitAction(DeleteList(0))

        return dispatcher
    }

    @Test
    fun `after a single delete, should have a snackBar message`() {
        val dispatcher = singleDeleteExample()
        val finalState = dispatcher.getCurrentState()

        finalState.snackbarMsg `should equal` MyListsMsg.listDeleted
    }

    @Test
    fun `after a single delete, should have pending deletions`() {
        val dispatcher = singleDeleteExample()
        val finalState = dispatcher.getCurrentState()

        val expectedDeletions = listOf(
            DeletedMetadata(item1, 0))

        finalState.pendingDeletions `should equal` expectedDeletions
    }

    @Test
    fun `after a single delete, DismissSnackbar can emit Delete side effect`() {
        val dispatcher = singleDeleteExample()

        dispatcher.getAllSideEffects().`should be empty`()

        dispatcher.submitAction(
            DismissSnackbar(false))

        val sideEffect = dispatcher.getLastSideEffect()

        val expectedDeletions = listOf(item1.name)
        sideEffect `should equal` MyListsSideEffect.Delete(expectedDeletions)
    }

    @Test
    fun `after a single delete, DismissSnackbar can flush pending deletion`() {
        val dispatcher = singleDeleteExample()
        dispatcher.submitAction(DismissSnackbar(false))
        val finalState = dispatcher.getCurrentState()

        val expectedLists = listOf(item2, item3, item4)
        finalState.lists  `should equal` LoadedLists.Ready(expectedLists)
        finalState.snackbarMsg.`should be null`()
        finalState.pendingDeletions.`should be empty`()
    }

    @Test
    fun `after a single delete, DismissSnackbar can undo deletion`() {
        val dispatcher = singleDeleteExample()
        dispatcher.submitAction(DismissSnackbar(true))
        val finalState = dispatcher.getCurrentState()

        val expectedLists = listOf(item1, item2, item3, item4)
        finalState.lists  `should equal` LoadedLists.Ready(expectedLists)
        finalState.snackbarMsg.`should be null`()
        finalState.pendingDeletions.`should be empty`()
    }

    @Test
    fun `after a single delete, DismissSnackbar can prevent Delete side effect`() {
        val dispatcher = singleDeleteExample()
        dispatcher.submitAction(DismissSnackbar(true))

        dispatcher.getAllSideEffects().`should be empty`()
    }

    private fun singleDeleteInMultiSelectExample(): TestDispatcher<MyListsState, MyListsSideEffect> {
        val initialState = MyListsState(
            lists = LoadedLists.Ready(
                listOf(
                    item1, item2, item3, item4
                )
            )
        )
        val dispatcher =
            TestDispatcher<MyListsState, MyListsSideEffect>(initialState)

        dispatcher.submitAction(StartSelection("List #1"))
        dispatcher.submitAction(ToggleSelection("List #4"))
        dispatcher.submitAction(DeleteList(3))

        return dispatcher
    }

    @Test
    fun `after a single delete in multi select, should remove item`() {
        val dispatcher = singleDeleteInMultiSelectExample()
        val finalState = dispatcher.getCurrentState()

        val expectedLists = listOf(
            SelectableLM(ListMetadata("List #1"), true),
            SelectableLM(ListMetadata("List #2"), false),
            SelectableLM(ListMetadata("List #3"), false)
        )

        finalState.lists `should equal` LoadedLists.MultiSelect(expectedLists)
    }

    @Test
    fun `after a single delete in multi select, should have a snackBar message`() {
        val dispatcher = singleDeleteInMultiSelectExample()
        val finalState = dispatcher.getCurrentState()

        finalState.snackbarMsg `should equal` MyListsMsg.listDeleted
    }

    @Test
    fun `after a single delete in multi select, should have pending deletions`() {
        val dispatcher = singleDeleteInMultiSelectExample()
        val finalState = dispatcher.getCurrentState()

        val expectedDeletions = listOf(
            DeletedMetadata(item4, 3))

        finalState.pendingDeletions `should equal` expectedDeletions
    }

    @Test
    fun `after a single delete in multi select, DismissSnackbar can emit Delete side effect`() {
        val dispatcher = singleDeleteInMultiSelectExample()

        dispatcher.getAllSideEffects().`should be empty`()

        dispatcher.submitAction(
            DismissSnackbar(false))

        val sideEffect = dispatcher.getLastSideEffect()

        val expectedDeletions = listOf(item4.name)
        sideEffect `should equal` MyListsSideEffect.Delete(expectedDeletions)
    }

    @Test
    fun `after a single delete in multi select, DismissSnackbar can flush pending deletion`() {
        val dispatcher = singleDeleteInMultiSelectExample()
        dispatcher.submitAction(DismissSnackbar(false))
        val finalState = dispatcher.getCurrentState()

        val expectedLists = listOf(
            SelectableLM(ListMetadata("List #1"), true),
            SelectableLM(ListMetadata("List #2"), false),
            SelectableLM(ListMetadata("List #3"), false)
        )
        finalState.lists  `should equal` LoadedLists.MultiSelect(expectedLists)
        finalState.snackbarMsg.`should be null`()
        finalState.pendingDeletions.`should be empty`()
    }

    @Test
    fun `after a single delete in multi select, DismissSnackbar can undo deletion`() {
        val dispatcher = singleDeleteInMultiSelectExample()
        dispatcher.submitAction(DismissSnackbar(true))
        val finalState = dispatcher.getCurrentState()

        val expectedLists = listOf(
            SelectableLM(ListMetadata("List #1"), true),
            SelectableLM(ListMetadata("List #2"), false),
            SelectableLM(ListMetadata("List #3"), false),
            SelectableLM(ListMetadata("List #4"), false)
        )
        finalState.lists  `should equal` LoadedLists.MultiSelect(expectedLists)
        finalState.snackbarMsg.`should be null`()
        finalState.pendingDeletions.`should be empty`()
    }
}

