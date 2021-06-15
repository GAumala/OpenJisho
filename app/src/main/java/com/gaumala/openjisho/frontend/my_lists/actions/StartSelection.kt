package com.gaumala.openjisho.frontend.my_lists.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.my_lists.LoadedLists
import com.gaumala.openjisho.frontend.my_lists.MyListsSideEffect
import com.gaumala.openjisho.frontend.my_lists.MyListsState
import com.gaumala.openjisho.frontend.my_lists.SelectableLM

class StartSelection(val listName: String)
    : Action<MyListsState, MyListsSideEffect>() {

    override fun update(state: MyListsState)
            : Update<MyListsState, MyListsSideEffect> {
        val loadedItems = state.lists as? LoadedLists.Ready
            ?: return Update(state)

        if (loadedItems.list.isEmpty())
            return Update(state)

        val selectableItems = loadedItems.list.map { item ->
            val isSelected = item.name == listName
            SelectableLM(item, isSelected)
        }

        val newState = state.copy(
            lists = LoadedLists.MultiSelect(selectableItems))

        return Update(newState)
    }
}