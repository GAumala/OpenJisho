package com.gaumala.openjisho.frontend.my_lists.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.my_lists.*

class DeleteList(val targetIndex: Int): Action<MyListsState, MyListsSideEffect>() {

    override fun update(state: MyListsState): Update<MyListsState, MyListsSideEffect> {
        val newState = when (val currentMetadata = state.lists) {
            is LoadedLists.MultiSelect -> computeNewState(state, currentMetadata)
            is LoadedLists.Ready -> computeNewState(state, currentMetadata)
            else -> return Update(state)
        }

        val listsToDelete = state.pendingDeletions.map { it.value.name }
        val sideEffect = if (listsToDelete.isEmpty()) null
            else MyListsSideEffect.Delete(listsToDelete)

        return Update(newState, sideEffect)
    }

    private fun computeNewState(state: MyListsState,
                                currentMetadata: LoadedLists.MultiSelect): MyListsState {
        val arrayList = ArrayList(currentMetadata.list)
        val deletedItem = arrayList.removeAt(targetIndex)

        val newDeletions = listOf(DeletedMetadata(
            value = deletedItem.metadata,
            deletedAt = targetIndex))

        return state.copy(
            lists = LoadedLists.MultiSelect(arrayList),
            pendingDeletions = newDeletions,
            snackbarMsg = MyListsMsg.listDeleted)
    }

    private fun computeNewState(state: MyListsState,
                                currentMetadata: LoadedLists.Ready): MyListsState {
        val arrayList = ArrayList(currentMetadata.list)
        val deletedMetadata = arrayList.removeAt(targetIndex)

        val newDeletions = listOf(DeletedMetadata(
            value = deletedMetadata,
            deletedAt = targetIndex))

        return state.copy(
            lists = LoadedLists.Ready(arrayList),
            pendingDeletions = newDeletions,
            snackbarMsg = MyListsMsg.listDeleted)
    }
}