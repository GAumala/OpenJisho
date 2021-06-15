package com.gaumala.openjisho.frontend.study_list

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.R
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.gaumala.openjisho.MainActivity
import com.gaumala.openjisho.SecondaryActivity
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.frontend.navigation.MainScreen
import com.gaumala.openjisho.frontend.navigation.Navigator
import com.gaumala.openjisho.frontend.navigation.SecondaryScreen
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.dict.DictSavedState
import com.gaumala.openjisho.frontend.entry.EntryFragment
import com.gaumala.openjisho.frontend.pages.ShowTextFragment
import com.gaumala.openjisho.utils.ui.AbstractSnackbar

class StudyListFragment: Fragment() {
    private lateinit var ui: StudyListUI
    private val viewModel by lazy {
        ViewModelProvider(this, StudyListViewModel.Factory(this))
            .get(StudyListViewModel::class.java)
    }

    private val name by lazy { requireArguments().getString(NAME_KEY) }

    private val navigator = object: StudyListNavigator {
        override fun goToJMdictEntry(summary: JMdictEntry.Summarized) {
            val mainActivity = activity as MainActivity

            val bundle = Bundle()
            bundle.putParcelable(
                EntryFragment.JMDICT_ENTRY_KEY, summary.entry)
            bundle.putString(
                EntryFragment.JMDICT_TITLE_KEY, summary.header)
            mainActivity.openSecondaryActivity(
                SecondaryScreen.showEntry, bundle)
        }

        override fun goToKanjidicEntry(entry: KanjidicEntry) {
            val mainActivity = activity as MainActivity

            val bundle = Bundle()
            bundle.putParcelable(EntryFragment.KANJIDIC_ENTRY_KEY, entry)
            mainActivity.openSecondaryActivity(
                SecondaryScreen.showEntry, bundle)
        }

        override fun goToTextDetail(text: String) {
            val mainActivity = activity as MainActivity

            val bundle = Bundle()
            bundle.putString(ShowTextFragment.HEADER_TEXT_KEY, text)
            mainActivity.openSecondaryActivity(
                SecondaryScreen.showText, bundle)
        }

        override fun goToSentence(japanese: String, english: String) {
            val mainActivity = activity as MainActivity

            val bundle = Bundle()
            bundle.putString(ShowTextFragment.HEADER_TEXT_KEY, japanese)
            bundle.putString(ShowTextFragment.SUB_TEXT_KEY, english)
            mainActivity.openSecondaryActivity(
                SecondaryScreen.showText, bundle)
        }

        override fun goToPickDictEntry(pickSentence: Boolean) {
            val mainActivity = activity as MainActivity

            val bundle = Bundle()
            bundle.putBoolean(DictFragment.IS_PICKER_KEY, true)
            mainActivity.openSecondaryActivity(
                SecondaryScreen.pickDictEntry, bundle)
        }

        override fun gotToComposeText() {
            val mainActivity = activity as MainActivity
            mainActivity.openSecondaryActivity(
                SecondaryScreen.composeText, Bundle())
        }
    }

    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val savedState = requireArguments()
                .getParcelable<DictSavedState>(DICT_SAVED_STATE_KEY)
            val screen = MainScreen.MyLists(savedState, reverse = true)

            val navigator = requireActivity() as Navigator
            navigator.goTo(screen)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        val act = requireActivity()
        act.onBackPressedDispatcher
            .addCallback(this, onBackPressedCallback)

        val view = inflater.inflate(
            R.layout.study_list_fragment, container, false)
        val snackBar = createSnackbar(view)

        ui = StudyListUI(owner = this.viewLifecycleOwner,
            view = view,
            navigator = navigator,
            snackBar = snackBar,
            sink = viewModel.userActionSink,
            liveState = viewModel.liveState)
        ui.subscribe()

        setupActivityToolbar(act as AppCompatActivity, view)

        return view
    }

    private fun createSnackbar(view: View): AbstractSnackbar {
        // speed dial's CoordinatorLayout holds the FAB
        val container = view.findViewById<ViewGroup>(R.id.speed_dial)
        return AbstractSnackbar.Default(container)
    }

    private fun setupActivityToolbar(
        activity: AppCompatActivity,
        view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = name
        activity.setSupportActionBar(toolbar)
    }

    fun processResult(data: Intent) {
        val id = System.currentTimeMillis()
        val resultText = data.getStringExtra(SecondaryActivity.RESULT_TEXT_KEY)
        if (resultText!= null) {
            ui.addTextItem(id, resultText)
            return
        }

        val resultJMdictEntry = data.getParcelableExtra<JMdictEntry.Summarized>(
            SecondaryActivity.RESULT_JMDICT_SUMMARIZED_KEY)
        if (resultJMdictEntry != null) {
            ui.addDictItem(id, resultJMdictEntry)
            return
        }

        val resultKanjidicEntry = data.getParcelableExtra<KanjidicEntry>(
            SecondaryActivity.RESULT_KANJIDIC_ENTRY_KEY)
        if (resultKanjidicEntry != null) {
            ui.addDictItem(id, resultKanjidicEntry)
            return
        }

        val resultSentence = data.getParcelableExtra<Sentence>(
            SecondaryActivity.RESULT_SENTENCE_KEY)
        if (resultSentence != null) {
            ui.addSentenceItem(id, resultSentence)
            return
        }
    }

    companion object {
        const val NAME_KEY = "name"
        private const val DICT_SAVED_STATE_KEY = "dictSavedState"

        fun newInstance(dictSavedState: DictSavedState,
                        name: String): StudyListFragment {
            val bundle = Bundle()
            bundle.putString(NAME_KEY, name)
            bundle.putParcelable(DICT_SAVED_STATE_KEY, dictSavedState)

            val f = StudyListFragment()
            f.arguments = bundle
            return f
        }
    }
}