package com.gaumala.openjisho.frontend.dict

import android.content.Intent
import android.os.Bundle
import com.gaumala.openjisho.MainActivity
import com.gaumala.openjisho.SecondaryActivity
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.frontend.navigation.SecondaryScreen
import com.gaumala.openjisho.frontend.entry.EntryFragment
import com.gaumala.openjisho.frontend.radicals.RadicalsFragment
import com.gaumala.openjisho.frontend.navigation.runEnterRadicalSearchTransition
import com.gaumala.openjisho.frontend.sentence.SentenceFragment

/**
 * A class that handles click events in [DictFragment].
 * The fragment does different things depending on the context, that
 * is why different implementations are created.
 */
abstract class DictClickHandler(protected val f: DictFragment) {

    abstract fun onRadicalSearchButtonClicked(savedState: DictSavedState, bottomTargets: List<Int>)
    abstract fun onJMdictEntryClicked(summarized: JMdictEntry.Summarized)
    abstract fun onKanjidicEntryClicked(entry: KanjidicEntry)
    abstract fun onSentenceClicked(sentence: Sentence)


    class Default(f: DictFragment): DictClickHandler(f) {

        override fun onRadicalSearchButtonClicked(savedState: DictSavedState, bottomTargets: List<Int>) {
            val nextFragment = RadicalsFragment.newInstance(savedState)
            val manager = f.parentFragmentManager

            manager.runEnterRadicalSearchTransition(f, nextFragment, bottomTargets)
        }

        override fun onJMdictEntryClicked(summarized: JMdictEntry.Summarized) {
            val activity = f.requireActivity() as MainActivity

            val bundle = Bundle()

            bundle.putParcelable(EntryFragment.JMDICT_ENTRY_KEY, summarized.entry)
            bundle.putString(EntryFragment.JMDICT_TITLE_KEY, summarized.header)

            activity.openSecondaryActivity(SecondaryScreen.showEntry, bundle)
        }

        override fun onKanjidicEntryClicked(entry: KanjidicEntry)  {
            val activity = f.requireActivity() as MainActivity

            val bundle = Bundle()
            bundle.putParcelable(EntryFragment.KANJIDIC_ENTRY_KEY, entry)

            activity.openSecondaryActivity(SecondaryScreen.showEntry, bundle)
        }

        override fun onSentenceClicked(sentence: Sentence) {
            val activity = f.requireActivity() as MainActivity

            val bundle = Bundle()
            bundle.putParcelable(SentenceFragment.SENTENCE_KEY, sentence)

            activity.openSecondaryActivity(SecondaryScreen.showSentence, bundle)
        }
    }
}