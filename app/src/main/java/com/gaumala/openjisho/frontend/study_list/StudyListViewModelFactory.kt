package com.gaumala.openjisho.frontend.study_list

import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gaumala.mvi.Dispatcher
import com.gaumala.openjisho.backend.lists.ListsDao
import com.gaumala.openjisho.frontend.my_lists.MyListsCache
import com.gaumala.openjisho.frontend.study_list.StudyListFragment.Companion.NAME_KEY
import com.gaumala.openjisho.utils.async.CoroutineIOWorker

class StudyListViewModelFactory(private val f: Fragment): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = StudyListViewModel()

        val ctx = f.requireContext()

        val name = f.requireArguments().getString(NAME_KEY)!!
        val initialState = StudyListState(
            name = name,
            cards = LoadedStudyCards.Loading)

        val worker = CoroutineIOWorker(viewModel.viewModelScope)
        val listsDao = ListsDao.Default(ctx)
        val sideEffectRunner = StudyListSERunner(worker, listsDao, name)
        val newDispatcher = Dispatcher(sideEffectRunner, initialState)

        // Wait for animation to finish before loading
        Handler().postDelayed({
            sideEffectRunner.runSideEffect(
                newDispatcher, StudyListSideEffect.Read)
        }, 300)

        viewModel.setDispatcher(newDispatcher)

        return viewModel as T
    }

}
