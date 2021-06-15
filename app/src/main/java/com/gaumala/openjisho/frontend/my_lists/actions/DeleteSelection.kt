package com.gaumala.openjisho.frontend.my_lists.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.my_lists.*

class DeleteSelection: Action<MyListsState, MyListsSideEffect>() {

    private fun computeNewDeletions(list: List<SelectableLM>)
            : List<DeletedMetadata> {
        val result = ArrayList<DeletedMetadata>()
        list.forEachIndexed { i, item ->
            if (item.isSelected) {
                val newDeletion = DeletedMetadata(item.metadata, i)
                result.add(newDeletion)
            }
        }
        return result
    }

    override fun update(state: MyListsState): Update<MyListsState, MyListsSideEffect> {
        val loadedMetadata = state.lists as? LoadedLists.MultiSelect
            ?: return Update(state)

        val newList = loadedMetadata.list
            .filter { !it.isSelected }
            .map { it.metadata }
        val newDeletions = computeNewDeletions(loadedMetadata.list)

        val newState = state.copy(
            snackbarMsg = MyListsMsg.selectionDeleted,
            pendingDeletions = newDeletions,
            lists = LoadedLists.Ready(newList)
        )

        val listsToDelete = state.pendingDeletions.map { it.value.name }
        val sideEffect = if (listsToDelete.isEmpty()) null
        else MyListsSideEffect.Delete(listsToDelete)

        return Update(newState, sideEffect)
    }
}