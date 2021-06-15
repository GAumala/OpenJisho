package com.gaumala.openjisho.frontend.my_lists.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.my_lists.LoadedLists
import com.gaumala.openjisho.frontend.my_lists.MyListsSideEffect
import com.gaumala.openjisho.frontend.my_lists.MyListsState

data class ToggleSelection(val targetName: String)
    : Action<MyListsState, MyListsSideEffect>() {

    override fun update(state: MyListsState)
            : Update<MyListsState, MyListsSideEffect> {
        val loadedLists =
            state.lists as? LoadedLists.MultiSelect ?: return Update(state)

        val selectableLists = loadedLists.list.map { item ->
            if (item.metadata.name == targetName)
                item.copy(isSelected = item.isSelected.not())
            else
                item
        }

        val newState = state.copy(
            lists = LoadedLists.MultiSelect(selectableLists))

        return Update(newState)
    }
}