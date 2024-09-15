package com.gaumala.openjisho.frontend.radicals

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gaumala.mvi.Dispatcher
import com.gaumala.mvi.DispatcherViewModel
import com.gaumala.openjisho.backend.db.DictDatabase

class RadicalsViewModel: DispatcherViewModel<RadicalsState, RadicalsSideEffect>() {

    class Factory(private val f: Fragment): ViewModelProvider.Factory {
        private fun getInitialState(): RadicalsState {
            return RadicalsState(
                radicals = RadicalIndex.listAll(),
                results = KanjiResults.Ready(emptyList())
            )
        }

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val ctx = f.activity as Context

            val viewModel = RadicalsViewModel()

            val appDB = DictDatabase.getInstance(ctx)
            val seRunner = RadicalsSERunner(
                viewModel.viewModelScope, appDB.dictQueryDao())

            val initialState = getInitialState()
            val newDispatcher = Dispatcher(seRunner, initialState)

            viewModel.setDispatcher(newDispatcher)
            return viewModel as T
        }
    }
}