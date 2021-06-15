package com.gaumala.openjisho.frontend.sentence

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.frontend.entry.EntryFragment
import com.gaumala.openjisho.frontend.navigation.runSlideTransition

/**
 * A fragment that displays a Tatoeba example sentence, its translation and
 * any JMdict entries associated with the words in the sentence.
 *
 * The user usually navigates here after clicking a sentence result row in
 * [com.gaumala.openjisho.frontend.dict.DictFragment].
 */
class SentenceFragment: Fragment() {
    companion object {
        const val SENTENCE_KEY = "sentence"

        fun newInstance(sentence: Sentence): SentenceFragment {
            val bundle = Bundle()
            bundle.putParcelable(SENTENCE_KEY, sentence)

            val fragment = SentenceFragment()
            fragment.arguments = bundle
            return fragment
        }
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
        val viewModel = ViewModelProviders.
        of(this, SentenceViewModel.Factory(this))
            .get(SentenceViewModel::class.java)

        val view = inflater.inflate(
            R.layout.sentence_fragment, container, false)
        setupToolbar(view)
        SentenceUI(
            showEntry = showEntry,
            owner = this.viewLifecycleOwner,
            view = view,
            liveState = viewModel.liveState
        ).subscribe()

        return view
    }

    private fun setupToolbar(view: View) {
        val activity = requireActivity() as AppCompatActivity
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        activity.setSupportActionBar(toolbar)

        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

}