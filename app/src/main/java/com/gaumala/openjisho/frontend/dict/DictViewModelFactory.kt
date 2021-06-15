package com.gaumala.openjisho.frontend.dict

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gaumala.mvi.ActionSink
import com.gaumala.openjisho.backend.db.DictDatabase
import com.gaumala.mvi.Dispatcher
import com.gaumala.openjisho.backend.dict.DictCache
import com.gaumala.openjisho.utils.async.CoroutineIOWorker
import com.gaumala.openjisho.utils.async.MessageThrottler

class DictViewModelFactory(private val f: Fragment,
                           private val savedInstanceState: Bundle?): ViewModelProvider.Factory {

    private fun getSavedState(): DictSavedState? =
        f.arguments!!.getParcelable(DictFragment.SAVED_STATE_KEY)
            ?: savedInstanceState?.getParcelable(DictFragment.SAVED_STATE_KEY)

    private fun runStartupEffects(runner: DictSideEffectRunner,
                                  sink: ActionSink<DictState, DictSideEffect>,
                                  initialState: DictState) {
        val params = DictSearchParams.createStartupSearchParams(initialState)
            ?: return

        // all side effects are throttled here because
        // I want to wait for transition to finish.
        // There's probably a more elegant way to do this.
        val sideEffect = DictSideEffect.Search(params, true)
        runner.runSideEffect(sink, sideEffect)
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val ctx = f.activity as Context
        val viewModel = DictViewModel()

        val appDB = DictDatabase.getInstance(ctx)
        val worker = CoroutineIOWorker(viewModel.viewModelScope)
        val dictCache = DictCache.Default(worker, appDB.dictQueryDao())
        val searchBroker = DictSearchBroker(dictCache)
        val dictThrottler = MessageThrottler(
            viewModel.viewModelScope,
            searchBroker,
            DictFragment.SEARCH_INTERVAL)
        val seRunner = DictSideEffectRunner(searchBroker, dictThrottler)

        val savedState: DictSavedState? = getSavedState()

        val initialState = DictState.fromSavedState(dictCache, savedState)
        val newDispatcher = Dispatcher(seRunner, initialState)
        viewModel.setDispatcher(newDispatcher)

        runStartupEffects(seRunner, newDispatcher, initialState)

        return viewModel as T
    }

}