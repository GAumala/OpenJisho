package com.gaumala.openjisho.frontend.my_lists

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gaumala.mvi.Dispatcher
import com.gaumala.mvi.DispatcherViewModel
import com.gaumala.openjisho.backend.lists.ListsDao
import com.gaumala.openjisho.utils.async.CoroutineIOWorker
import kotlinx.coroutines.CoroutineScope

class MyListsViewModel: DispatcherViewModel<MyListsState, MyListsSideEffect>() {
    class Factory(private val f: Fragment): ViewModelProvider.Factory {

        private fun createDispatcher(scope: CoroutineScope)
                : Dispatcher<MyListsState, MyListsSideEffect> {
            val ctx = f.requireContext()
            val worker = CoroutineIOWorker(scope)
            val listsDao = ListsDao.Default(ctx)
            val listsCache = MyListsCache.Default(worker, listsDao)
            val sideEffectRunner = MyListsSERunner(worker, listsDao, listsCache)

            val cachedLists = listsCache.getCachedLists()
            if (cachedLists != null) {
                val initialState = MyListsState(
                    lists = LoadedLists.Ready(cachedLists)
                )
                return Dispatcher(sideEffectRunner, initialState)
            }

            val initialState = MyListsState(lists = LoadedLists.Loading)
            val newDispatcher = Dispatcher(sideEffectRunner, initialState)

            sideEffectRunner.runSideEffect(
                newDispatcher, MyListsSideEffect.Load)

            return newDispatcher
        }

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val viewModel = MyListsViewModel()
            val newDispatcher = createDispatcher(viewModel.viewModelScope)
            viewModel.setDispatcher(newDispatcher)

            return viewModel as T
        }

    }
}