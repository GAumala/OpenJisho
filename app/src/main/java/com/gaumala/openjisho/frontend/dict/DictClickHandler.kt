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
import com.gaumala.openjisho.frontend.navigation.runDictToRadicalsTransition
import com.gaumala.openjisho.frontend.pages.ShowTextFragment
import com.gaumala.openjisho.frontend.sentence.SentenceFragment

/**
 * A class that handles click events in [DictFragment].
 * The fragment does different things depending on the context, that
 * is why different implementations are created.
 */
abstract class DictClickHandler(protected val f: DictFragment) {

    abstract fun onRadicalSearchButtonClicked(savedState: DictSavedState?)
    abstract fun onJMdictEntryClicked(summarized: JMdictEntry.Summarized)
    abstract fun onKanjidicEntryClicked(entry: KanjidicEntry)
    abstract fun onSentenceClicked(sentence: Sentence)


    class Default(f: DictFragment): DictClickHandler(f) {

        override fun onRadicalSearchButtonClicked(savedState: DictSavedState?) {
            val nextFragment = RadicalsFragment.newInstance(savedState, false)
            val manager = f.parentFragmentManager

            manager.runDictToRadicalsTransition(f, nextFragment)
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

    class Picker(f: DictFragment): DictClickHandler(f) {

        override fun onRadicalSearchButtonClicked(savedState: DictSavedState?) {
            val nextFragment = RadicalsFragment.newInstance(savedState, true)
            val manager = f.parentFragmentManager

            manager.runDictToRadicalsTransition(f, nextFragment)
        }

        override fun onJMdictEntryClicked(summarized: JMdictEntry.Summarized) {
            val intent = Intent()
            intent.putExtra(SecondaryActivity.RESULT_JMDICT_SUMMARIZED_KEY, summarized)

            val activity = f.requireActivity() as SecondaryActivity
            activity.submitOKResult(intent)
        }

        override fun onKanjidicEntryClicked(entry: KanjidicEntry)  {
            val intent = Intent()
            intent.putExtra(SecondaryActivity.RESULT_KANJIDIC_ENTRY_KEY, entry)

            val activity = f.requireActivity() as SecondaryActivity
            activity.submitOKResult(intent)
        }

        override fun onSentenceClicked(sentence: Sentence) {
            val intent = Intent()
            intent.putExtra(SecondaryActivity.RESULT_SENTENCE_KEY, sentence)

            val activity = f.requireActivity() as SecondaryActivity
            activity.submitOKResult(intent)
        }
    }
}