package com.gaumala.openjisho.frontend.user_sentence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Transition
import androidx.transition.TransitionSet
import com.gaumala.mvi.ActionSink
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.entry.EntryFragment
import com.gaumala.openjisho.frontend.navigation.runEnterRadicalSearchTransition
import com.gaumala.openjisho.frontend.navigation.runSlideTransition
import com.gaumala.openjisho.frontend.radicals.RadicalsFragment
import com.gaumala.openjisho.utils.parcelable

/**
 * A fragment that lets the user input a sentence and query JMdict entries associated
 * with the words in the sentence, just like the tatoeba sentences.
 *
 * This fragment is displayed when user clicks "Input Sentence" in the drawer menu.
 */
class UserSentenceFragment : Fragment() {
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

    private val onRadicalSearchButtonClicked = { sentence: String, bottomTargets: List<Int> ->
        val savedState = UserSentenceSavedState(sentence)
        val nextFragment = RadicalsFragment.newInstance(savedState)

        parentFragmentManager.runEnterRadicalSearchTransition(this, nextFragment, bottomTargets)
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
        setupTransitionListener()
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
            isTransitioning = enterTransition != null,
            liveState = viewModel.liveState,
        )
        ui.subscribe()

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val savedState = ui.saveState()
        outState.putParcelable(SAVED_STATE_KEY, savedState)
    }

    private fun getInitialText(savedInstanceState: Bundle?): String {
        val savedState: UserSentenceSavedState? = savedInstanceState?.parcelable(SAVED_STATE_KEY)
            ?: arguments?.parcelable(SAVED_STATE_KEY)
        return savedState?.sentence ?: ""
    }
    private fun setupTransitionListener() {
        val transition = enterTransition as? TransitionSet ?: return
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
            }

            override fun onTransitionEnd(transition: Transition) {
                ui.onTransitionEnd()
                transition.removeListener(this)
            }

            override fun onTransitionCancel(transition: Transition) {
            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionResume(transition: Transition) {
            }

        })
    }
}