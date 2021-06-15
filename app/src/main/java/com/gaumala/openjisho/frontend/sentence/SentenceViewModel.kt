package com.gaumala.openjisho.frontend.sentence

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gaumala.mvi.Dispatcher
import com.gaumala.mvi.DispatcherViewModel
import com.gaumala.openjisho.backend.db.DictDatabase
import com.gaumala.openjisho.common.Sentence

class SentenceViewModel: DispatcherViewModel<SentenceState, SentenceSideEffect>() {

    class Factory(val f: Fragment): ViewModelProvider.Factory {
        private fun createInitialState(): SentenceState {
            val args = f.requireArguments()
            val sentence =
                args.getParcelable<Sentence>(SentenceFragment.SENTENCE_KEY)!!

            return SentenceState(sentence)
        }

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val ctx = f.activity as Context
            val appDB = DictDatabase.getInstance(ctx)
            val initialState = createInitialState()
            val viewModel = SentenceViewModel()

            val runner = SentenceSideEffectRunner(
                viewModel.viewModelScope,
                appDB.dictQueryDao()
            )
            val newDispatcher = Dispatcher(runner, initialState)

            val startupSideEffect =
                SentenceSideEffect.LoadIndices(initialState.sentence.id)
            runner.runSideEffect(newDispatcher, startupSideEffect)

            viewModel.setDispatcher(newDispatcher)
            return viewModel as T
        }
    }
}
