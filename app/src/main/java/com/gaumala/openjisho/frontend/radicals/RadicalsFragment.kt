package com.gaumala.openjisho.frontend.radicals

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.dict.DictSavedState
import com.gaumala.openjisho.frontend.navigation.runExitRadicalSearchTransition
import com.gaumala.openjisho.frontend.user_sentence.UserSentenceFragment
import com.gaumala.openjisho.frontend.user_sentence.UserSentenceSavedState
import com.gaumala.openjisho.utils.parcelable

/**
 * A fragment used for looking up kanji by radicals.
 *
 * The user can only navigate here from the button next the the search text
 * input in [DictFragment].
 */
class RadicalsFragment : Fragment() {

    companion object {
        fun newInstance(
            savedState: Parcelable?,
            isPicker: Boolean
        ): RadicalsFragment {
            val args = Bundle()
            args.putParcelable(PREV_SCREEN_SAVED_STATE_KEY, savedState)
            args.putBoolean(IS_PICKER_KEY, isPicker)

            val f = RadicalsFragment()
            f.arguments = args
            return f
        }

        const val PREV_SCREEN_SAVED_STATE_KEY = "prevScreenSavedState"
        const val IS_PICKER_KEY = "isPicker"
        const val QUERY_TEXT_KEY = "queryText"
    }

    private lateinit var ui: RadicalsUI

    private val viewModel by lazy {
        ViewModelProvider(this, RadicalsViewModel.Factory(this))
            .get(RadicalsViewModel::class.java)
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            ui.onBackPressed()
        }
    }

    private val exitRadicalSearch = { queryText: String ->
        val args = requireArguments()
        val isPicker = args.getBoolean(IS_PICKER_KEY)
        val savedState: Parcelable? =
            args.parcelable(PREV_SCREEN_SAVED_STATE_KEY)
        val nextFragment = when (savedState) {
            is DictSavedState -> {
                val updatedState = DictSavedState.updateQuery(savedState, queryText)

                DictFragment.newInstance(
                    delayKeyboardBy = 600,
                    savedState = updatedState,
                    isPicker = isPicker
                )
            }

            is UserSentenceSavedState -> {
                UserSentenceFragment.newInstance(savedState.copy(sentence = queryText))
            }

            else -> {
                throw IllegalStateException("Unknown saved state $savedState")
            }
        }

        parentFragmentManager.runExitRadicalSearchTransition(
            this, nextFragment
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val act = requireActivity()
        act.onBackPressedDispatcher
            .addCallback(this.viewLifecycleOwner, onBackPressedCallback)

        val view = inflater.inflate(
            R.layout.radicals_fragment, container, false
        )

        ui = RadicalsUI(
            owner = this.viewLifecycleOwner,
            view = view,
            returnToDict = exitRadicalSearch,
            sink = viewModel.userActionSink,
            liveState = viewModel.liveState,
            initialText = getInitialText(savedInstanceState)
        )
        ui.subscribe()

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(QUERY_TEXT_KEY, ui.getQueryText())
    }

    private fun getInitialText(savedInstanceState: Bundle?): String {
        val savedText = savedInstanceState?.getString(QUERY_TEXT_KEY)
        if (savedText != null) return savedText

        val savedState: Parcelable? = requireArguments()
            .parcelable(PREV_SCREEN_SAVED_STATE_KEY)
        return when (savedState) {
            is DictSavedState -> savedState.queryText
            is UserSentenceSavedState -> savedState.sentence
            else -> throw IllegalArgumentException("Unknown saved state $savedState")
        }
    }
}