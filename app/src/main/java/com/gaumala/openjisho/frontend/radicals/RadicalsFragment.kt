package com.gaumala.openjisho.frontend.radicals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.dict.DictSavedState
import com.gaumala.openjisho.frontend.navigation.runRadicalsToDictTransition

/**
 * A fragment used for looking up kanji by radicals.
 *
 * The user can only navigate here from the button next the the search text
 * input in [DictFragment].
 */
class RadicalsFragment : Fragment() {

    companion object {
        fun newInstance(savedState: DictSavedState?,
                        isPicker: Boolean): RadicalsFragment {
            val args = Bundle()
            args.putParcelable(DICT_SAVED_STATE_KEY, savedState)
            args.putBoolean(IS_PICKER_KEY, isPicker)

            val f = RadicalsFragment()
            f.arguments = args
            return f
        }

        const val DICT_SAVED_STATE_KEY = "dictSavedState"
        const val IS_PICKER_KEY = "isPicker"
    }

    private lateinit var ui: RadicalsUI

    private val viewModel by lazy {
        ViewModelProvider(this, RadicalsViewModelFactory(this))
            .get(RadicalsViewModel::class.java)
    }
    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            ui.onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        val act = requireActivity()
        act.onBackPressedDispatcher
            .addCallback(this, onBackPressedCallback)

        val view = inflater.inflate(
            R.layout.radicals_fragment, container, false)

        ui = RadicalsUI(owner = this.viewLifecycleOwner,
            view = view,
            returnToDict = returnToDict,
            sink = viewModel.userActionSink,
            liveState = viewModel.liveState)
        ui.subscribe()

        return view
    }

    private val returnToDict = { queryText: String ->
        val args = requireArguments()
        val isPicker = args.getBoolean(IS_PICKER_KEY)
        val savedState =
            args.getParcelable<DictSavedState?>(DICT_SAVED_STATE_KEY)
        val updatedState = DictSavedState.updateQuery(savedState, queryText)

        val nextFragment = DictFragment.newInstance(
            delayKeyboardBy = 600,
            savedState = updatedState,
            isPicker = isPicker)

        parentFragmentManager.runRadicalsToDictTransition(
            this, nextFragment)
    }
}