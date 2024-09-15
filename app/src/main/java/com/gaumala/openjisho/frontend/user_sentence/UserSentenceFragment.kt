package com.gaumala.openjisho.frontend.user_sentence

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.gaumala.mvi.ActionSink
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.dict.DictSavedState
import com.gaumala.openjisho.frontend.entry.EntryFragment
import com.gaumala.openjisho.frontend.navigation.runEnterRadicalSearchTransition
import com.gaumala.openjisho.frontend.navigation.runSlideTransition
import com.gaumala.openjisho.frontend.radicals.RadicalsFragment
import com.gaumala.openjisho.frontend.radicals.RadicalsFragment.Companion.PREV_SCREEN_SAVED_STATE_KEY
import com.gaumala.openjisho.frontend.radicals.RadicalsFragment.Companion.QUERY_TEXT_KEY
import com.gaumala.openjisho.frontend.user_sentence.actions.SetSentence
import com.gaumala.openjisho.utils.parcelable

/**
 * A fragment that lets the user input a sentence and query JMdict entries associated
 * with the words in the sentence, just like the tatoeba sentences.
 *
 * This fragment is displayed when user clicks "Input Sentence" in the drawer menu.
 */
class UserSentenceFragment : Fragment(), InputSentenceDialogParent {
    companion object {
        const val SAVED_TEXT_KEY = "savedText"
        const val SAVED_STATE_KEY = "savedState"
        const val SEARCH_INTERVAL = 800L

        fun newInstance(savedState: UserSentenceSavedState?): UserSentenceFragment =
            UserSentenceFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SAVED_STATE_KEY, savedState)
                }
            }
    }

    private lateinit var ui: UserSentenceUI
    private lateinit var actionSink: ActionSink<UserSentenceState, UserSentenceSideEffect>

    private val showEntry = { summarized: JMdictEntry.Summarized ->
        val nextFragment = EntryFragment.newInstance(
            entry = summarized.entry,
            title = summarized.header
        )

        requireActivity().supportFragmentManager.runSlideTransition(
            newFragment = nextFragment,
            addToBackStack = true
        )
    }

    private val onRadicalSearchButtonClicked = { sentence: String ->
        val savedState = UserSentenceSavedState(sentence)
        val nextFragment = RadicalsFragment.newInstance(savedState, false)

        parentFragmentManager.runEnterRadicalSearchTransition(this, nextFragment)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val nextFragment = DictFragment.newInstance(delayKeyboardBy = 600)
            requireActivity().supportFragmentManager.runSlideTransition(
                newFragment = nextFragment,
                reverse = true
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val act = requireActivity()
        act.onBackPressedDispatcher
            .addCallback(this.viewLifecycleOwner, onBackPressedCallback)

        val factory = UserSentenceViewModel.Factory(this, savedInstanceState)
        val viewModel = ViewModelProviders.of(this, factory)
            .get(UserSentenceViewModel::class.java)

        val view = inflater.inflate(
            R.layout.user_sentence_fragment, container, false
        )
        actionSink = viewModel.userActionSink
        ui = UserSentenceUI(
            showEntry = showEntry,
            sink = viewModel.userActionSink,
            owner = this.viewLifecycleOwner,
            onRadicalSearchButtonClicked = onRadicalSearchButtonClicked,
            onBackPressedCallback = onBackPressedCallback,
            initialText = getInitialText(savedInstanceState),
            view = view,
            liveState = viewModel.liveState,
        )
        ui.subscribe()

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val savedText = ui.getSavedText()
        outState.putString(SAVED_TEXT_KEY, savedText)
    }

    override fun onInputSentence(sentence: String) {
        actionSink.submitAction(SetSentence(sentence))
    }

    private fun getInitialText(savedInstanceState: Bundle?): String {
        val savedText = savedInstanceState?.getString(SAVED_TEXT_KEY)
        if (savedText != null) return savedText

        val savedState: UserSentenceSavedState? = arguments?.parcelable(SAVED_STATE_KEY)
        return savedState?.sentence ?: ""
    }
}