package com.gaumala.openjisho.frontend.my_lists.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.my_lists.LoadedLists
import com.gaumala.openjisho.frontend.my_lists.MyListsSideEffect
import com.gaumala.openjisho.frontend.my_lists.MyListsState

class CancelSelection: Action<MyListsState, MyListsSideEffect>() {

    override fun update(state: MyListsState)
            : Update<MyListsState, MyListsSideEffect> {
        val loadedLists = state.lists as? LoadedLists.MultiSelect
            ?: return Update(state)

        val newItems = loadedLists.list.map { item ->
            item.metadata
        }

        val newState = state.copy(
            lists = LoadedLists.Ready(newItems))

        return Update(newState)
    }
}