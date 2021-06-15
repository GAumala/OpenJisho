package com.gaumala.openjisho.frontend.my_lists.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.my_lists.*

class DismissSnackbar(val restoreBackup: Boolean)
    : Action<MyListsState, MyListsSideEffect>() {

    override fun update(state: MyListsState)
            : Update<MyListsState, MyListsSideEffect> {

        if (!restoreBackup)
            return updateCommittingDeletions(state)

        val restoredState =
            when (val currentMetadata = state.lists) {
                is LoadedLists.Ready ->
                    computeRestoredState(state, currentMetadata)
                is LoadedLists.MultiSelect ->
                    computeRestoredState(state, currentMetadata)
                else -> state.copy(
                    snackbarMsg = null,
                    pendingDeletions = emptyList()
                )
            }
        return Update(restoredState)
    }

    private fun updateCommittingDeletions(state: MyListsState)
            : Update<MyListsState, MyListsSideEffect> {
        val newState = state.copy(
            snackbarMsg = null,
            pendingDeletions = emptyList()
        )

        val listsToDelete = state.pendingDeletions.map { it.value.name }
        val sideEffect = if (listsToDelete.isEmpty()) null
        else MyListsSideEffect.Delete(listsToDelete)

        return Update(newState, sideEffect)
    }

    private fun computeRestoredState(state: MyListsState,
                                     currentMetadata: LoadedLists.Ready)
            : MyListsState {
        val restoredList = ArrayList(currentMetadata.list)
        state.pendingDeletions.forEach {
            restoredList.add(it.deletedAt, it.value)
        }

        return state.copy(
            lists = LoadedLists.Ready(restoredList),
            pendingDeletions = emptyList(),
            snackbarMsg = null)
    }
    private fun computeRestoredState(state: MyListsState,
                                     currentMetadata: LoadedLists.MultiSelect)
            : MyListsState {
        val restoredList = ArrayList(currentMetadata.list)
        state.pendingDeletions.forEach {
            val restoredItem = SelectableLM(it.value, false)
            restoredList.add(it.deletedAt, restoredItem)
        }

        return state.copy(
            lists = LoadedLists.MultiSelect(restoredList),
            pendingDeletions = emptyList(),
            snackbarMsg = null)
    }
}