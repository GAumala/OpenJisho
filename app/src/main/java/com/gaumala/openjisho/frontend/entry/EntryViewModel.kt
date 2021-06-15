package com.gaumala.openjisho.frontend.entry

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gaumala.mvi.Dispatcher
import com.gaumala.mvi.DispatcherViewModel
import com.gaumala.openjisho.backend.db.DictDatabase
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry

class EntryViewModel: DispatcherViewModel<EntryState, EntrySideEffect>() {

    class Factory(val f: Fragment): ViewModelProvider.Factory {
        private fun createInitialState(): EntryState {
            val args = f.requireArguments()
            val jmDictEntry = args.getParcelable<JMdictEntry>(EntryFragment.JMDICT_ENTRY_KEY)
            val title = args.getString(EntryFragment.JMDICT_TITLE_KEY)

            if (jmDictEntry != null)
                return EntryState(Section.fromJMdictEntry(jmDictEntry, title))

            val kanjidicEntry = args
                .getParcelable<KanjidicEntry>(EntryFragment.KANJIDIC_ENTRY_KEY)
            return EntryState(Section.fromKanjidicEntry(kanjidicEntry))
        }

        private fun createStartupSideEffect(): EntrySideEffect? {
            val jmDictEntry = f.arguments!!
                .getParcelable<JMdictEntry>(EntryFragment.JMDICT_ENTRY_KEY)

            if (jmDictEntry != null)
                return EntrySideEffect.SearchKanji(jmDictEntry.kanjiElements)

            return null
        }

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val ctx = f.activity as Context

            val viewModel = EntryViewModel()

            val initialState = createInitialState()
            val appDB = DictDatabase.getInstance(ctx)
            val runner = EntrySideEffectRunner(
                viewModel.viewModelScope, appDB.dictQueryDao())
            val newDispatcher = Dispatcher(runner, initialState)

            val startupSideEffect = createStartupSideEffect()
            if (startupSideEffect != null)
                runner.runSideEffect(newDispatcher, startupSideEffect)

            viewModel.setDispatcher(newDispatcher)
            return viewModel as T
        }
    }
}