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
import com.gaumala.openjisho.frontend.user_sentence.UserSentenceFragment.Companion.SEARCH_INTERVAL
import com.gaumala.openjisho.utils.async.MessageThrottler
import com.gaumala.openjisho.utils.parcelable

class UserSentenceViewModel : DispatcherViewModel<UserSentenceState, UserSentenceSideEffect>() {

    class Factory(
        val f: Fragment,
        private val savedInstanceState: Bundle?
    ) : ViewModelProvider.Factory {
        private fun createInitialState(): UserSentenceState {
            val savedState =
                f.arguments?.parcelable<UserSentenceSavedState>(UserSentenceFragment.SAVED_STATE_KEY)
            val initialText = savedInstanceState?.getString(UserSentenceFragment.SAVED_TEXT_KEY)
                ?: savedState?.sentence
            return UserSentenceState(initialText ?: "")
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val ctx = f.activity as Context
            val appDB = DictDatabase.getInstance(ctx)
            val initialState = createInitialState()
            val viewModel = UserSentenceViewModel()

            val searchDelegate = WordSearchDelegate(appDB.dictQueryDao())
            val searchThrottle = MessageThrottler(
                scope = viewModel.viewModelScope,
                receiver = searchDelegate,
                interval = SEARCH_INTERVAL
            )

            val runner = UserSentenceSERunner(searchThrottle)
            val newDispatcher = Dispatcher(runner, initialState)

            if (initialState.text.isNotEmpty()) {
                val startupSideEffect =
                    UserSentenceSideEffect.LoadWords(initialState.text)
                runner.runSideEffect(newDispatcher, startupSideEffect)
            }

            viewModel.setDispatcher(newDispatcher)
            return viewModel as T
        }
    }
}
