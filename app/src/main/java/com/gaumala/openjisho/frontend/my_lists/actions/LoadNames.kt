package com.gaumala.openjisho.frontend.my_lists.actions

import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.my_lists.ListMetadata
import com.gaumala.openjisho.frontend.my_lists.LoadedLists
import com.gaumala.openjisho.frontend.my_lists.MyListsSideEffect
import com.gaumala.openjisho.frontend.my_lists.MyListsState

data class LoadNames(val newList: List<ListMetadata>?): Action<MyListsState, MyListsSideEffect>() {

    override fun update(state: MyListsState): Update<MyListsState, MyListsSideEffect> {
        if (state.lists !is LoadedLists.Loading)
            return Update(state)

        val newLoadedMetadata = if (newList != null)
            LoadedLists.Ready(newList)
        else
            LoadedLists.Error(R.string.failed_to_load_lists)

        return Update(state.copy(lists = newLoadedMetadata))
    }

}