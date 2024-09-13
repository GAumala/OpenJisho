package com.gaumala.openjisho.frontend.user_sentence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.gaumala.mvi.ActionSink
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.frontend.entry.EntryFragment
import com.gaumala.openjisho.frontend.navigation.runSlideTransition
import com.gaumala.openjisho.frontend.user_sentence.actions.SetSentence

/**
 * A fragment that displays a sentence input by the user along with
 * any JMdict entries associated with the words in the sentence. The UI
 * is meant to be the same as
 * [com.gaumala.openjisho.frontend.sentence.SentenceFragment]
 *
 * This fragment is displayed when user clicks "Input Sentence" in the drawer menu.
 */
class UserSentenceFragment : Fragment(), InputSentenceDialogParent {
    companion object {
        const val INITIAL_TEXT_KEY = "initialText"
        const val SAVED_TEXT_KEY = "savedText"

        fun newInstance(initialText: String): UserSentenceFragment {
            val bundle = Bundle()
            bundle.putString(INITIAL_TEXT_KEY, initialText)

            val fragment = UserSentenceFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var ui: UserSentenceUI
    lateinit var actionSink: ActionSink<UserSentenceState, UserSentenceSideEffect>

    private val editSentence = { initialValue: String ->
        InputSentenceWidget(childFragmentManager).prompt(initialValue)
    }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
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
        val factory = UserSentenceViewModel.Factory(this, savedInstanceState)
        val viewModel = ViewModelProviders.of(this, factory)
            .get(UserSentenceViewModel::class.java)

        val view = inflater.inflate(
            R.layout.sentence_fragment, container, false
        )
        setupToolbar(view)
        actionSink = viewModel.userActionSink
        ui = UserSentenceUI(
            showEntry = showEntry,
            editSentence = editSentence,
            owner = this.viewLifecycleOwner,
            view = view,
            liveState = viewModel.liveState
        )
        ui.subscribe()

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val savedText = ui.getSavedText()
        outState.putString(SAVED_TEXT_KEY, savedText)
    }


    private fun setupToolbar(view: View) {
        val activity = requireActivity() as AppCompatActivity
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        activity.setSupportActionBar(toolbar)

        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onInputSentence(sentence: String) {
        actionSink.submitAction(SetSentence(sentence))
    }
}