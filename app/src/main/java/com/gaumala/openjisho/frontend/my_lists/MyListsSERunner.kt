package com.gaumala.openjisho.frontend.my_lists

import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.backend.lists.ListsDao
import com.gaumala.openjisho.frontend.my_lists.actions.LoadNames
import com.gaumala.openjisho.utils.async.AsyncWorker
import com.gaumala.openjisho.utils.error.Either

class MyListsSERunner(private val worker: AsyncWorker,
                      private val dao: ListsDao,
                      private val listsCache: MyListsCache)
    : SideEffectRunner<MyListsState, MyListsSideEffect> {

    private fun deleteListWorkload(names: List<String>) = { ->
        names.forEach {
            dao.deleteListByName(it)
        }
    }

    override fun runSideEffect(
        sink: ActionSink<MyListsState, MyListsSideEffect>,
        sideEffect: MyListsSideEffect) {

        if (sideEffect == MyListsSideEffect.Load)
            loadLists(sink)

        else if (sideEffect is MyListsSideEffect.Delete)
            deleteList(sink, sideEffect)
    }

    private fun deleteList(
        sink: ActionSink<MyListsState, MyListsSideEffect>,
        sideEffect: MyListsSideEffect.Delete) {
        worker.workInBackground(deleteListWorkload(sideEffect.names)) {
            listsCache.reload()
        }
    }

    private fun loadLists(sink: ActionSink<MyListsState, MyListsSideEffect>) {
        listsCache.loadLists { res ->
            val action = when (res) {
                is Either.Right -> LoadNames(res.value)
                is Either.Left -> LoadNames(null)
            }
            sink.submitAction(action)
        }
    }

}