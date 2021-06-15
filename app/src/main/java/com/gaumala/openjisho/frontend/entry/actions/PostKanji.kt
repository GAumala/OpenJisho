package com.gaumala.openjisho.frontend.entry.actions

import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.frontend.entry.EntrySideEffect
import com.gaumala.openjisho.frontend.entry.EntryState
import com.gaumala.mvi.Action
import com.gaumala.mvi.Update
import com.gaumala.openjisho.frontend.entry.Section

data class PostKanji(val kanjiEntries: List<KanjidicEntry>): Action<EntryState, EntrySideEffect>() {

    override fun update(state: EntryState): Update<EntryState, EntrySideEffect> {
        if (kanjiEntries.isEmpty())
            return Update(state)

        val kanjiSection = Section.Kanji(kanjiEntries)
        val newSections = state.sections.plus(kanjiSection)
        return Update(state.copy(sections = newSections))
   }

}