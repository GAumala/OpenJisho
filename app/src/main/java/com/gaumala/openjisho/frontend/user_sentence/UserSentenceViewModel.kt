package com.gaumala.openjisho.frontend.user_sentence

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gaumala.mvi.Dispatcher
import com.gaumala.mvi.DispatcherViewModel
import com.gaumala.openjisho.backend.db.DictDatabase

class UserSentenceViewModel : DispatcherViewModel<UserSentenceState, UserSentenceSideEffect>() {

    class Factory(
        val f: Fragment,
        private val savedInstanceState: Bundle?
    ) : ViewModelProvider.Factory {
        private fun createInitialState(): UserSentenceState {
            val args = f.requireArguments()
            val initialText = savedInstanceState?.getString(UserSentenceFragment.SAVED_TEXT_KEY)
                ?: args.getString(UserSentenceFragment.INITIAL_TEXT_KEY)!!

            return UserSentenceState(initialText)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val ctx = f.activity as Context
            val appDB = DictDatabase.getInstance(ctx)
            val initialState = createInitialState()
            val viewModel = UserSentenceViewModel()

            val runner = UserSentenceSERunner(
                viewModel.viewModelScope,
                appDB.dictQueryDao()
            )
            val newDispatcher = Dispatcher(runner, initialState)

            val startupSideEffect =
                UserSentenceSideEffect.LoadWords(initialState.text)
            runner.runSideEffect(newDispatcher, startupSideEffect)

            viewModel.setDispatcher(newDispatcher)
            return viewModel as T
        }
    }
}
