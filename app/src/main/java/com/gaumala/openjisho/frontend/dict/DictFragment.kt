package com.gaumala.openjisho.frontend.dict

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.Transition
import androidx.transition.TransitionSet
import com.gaumala.openjisho.R
import com.gaumala.openjisho.backend.VersionManager
import com.gaumala.openjisho.frontend.dict.recycler.DictItemFactory
import com.gaumala.openjisho.frontend.history.DictHistoryWidget
import com.gaumala.openjisho.frontend.navigation.NavDrawerContainer
import com.gaumala.openjisho.utils.SystemUIHelper
import com.gaumala.openjisho.utils.parcelable
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


/**
 * The main fragment of the app. Here is where the user can
 * lookup words or sentences.
 */
class DictFragment : Fragment() {

    companion object {
        fun newInstance(
            delayKeyboardBy: Long = 0,
            savedState: DictSavedState? = null
        ): DictFragment {
            val args = Bundle()
            args.putLong(DELAY_KEYBOARD_BY_KEY, delayKeyboardBy)
            args.putParcelable(SAVED_STATE_KEY, savedState)

            val f = DictFragment()
            f.arguments = args
            return f
        }


        const val DELAY_KEYBOARD_BY_KEY = "delayKeyboardBy"
        const val SAVED_STATE_KEY = "dictSavedState"

        const val SEARCH_INTERVAL = 800L
    }

    private lateinit var ui: DictUI

    private val delayKeyboardBy by lazy {
        requireArguments().getLong(DELAY_KEYBOARD_BY_KEY)
    }

    private val layout = R.layout.dict_fragment

    private val drawerContainer by lazy {
        requireActivity() as? NavDrawerContainer
    }

    private val dictClickHandler = DictClickHandler.Default(this)

    private val historyWidget by lazy {
        DictHistoryWidget(this)
    }

    private val onNewVersionNotificationClicked: () -> Unit = {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(VersionManager.UPDATE_URL)
        )
        startActivity(browserIntent)

    }

    private val itemFactory by lazy {
        DictItemFactory(
            onJMdictEntryClicked = { entry ->
                dictClickHandler.onJMdictEntryClicked(entry)
                historyWidget.push(entry.header)
            },
            onKanjidicEntryClicked = { entry ->
                dictClickHandler.onKanjidicEntryClicked(entry)
            },
            onSentenceClicked = { sentence ->
                dictClickHandler.onSentenceClicked(sentence)
            },
            onSuggestionClicked = { suggestedQuery ->
                inputQueryText(suggestedQuery)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupTransitionListener()
        setHasOptionsMenu(true)

        val factory = DictViewModel.Factory(this, savedInstanceState)
        val viewModel =
            ViewModelProvider(this, factory)
                .get(DictViewModel::class.java)

        val view = inflater.inflate(
            layout, container, false
        )

        ui = DictUI(
            owner = this.viewLifecycleOwner,
            delayKeyboardBy = delayKeyboardBy,
            itemFactory = itemFactory,
            dictClickHandler = dictClickHandler,
            view = view,
            historyWidget = historyWidget,
            sink = viewModel.userActionSink,
            drawerContainer = requireActivity() as? NavDrawerContainer,
            savedState = requireArguments().parcelable(SAVED_STATE_KEY),
            isTransitioning = enterTransition != null,
            liveState = viewModel.liveState
        )
        ui.subscribe()

        SystemUIHelper(this).matchWithPrimary()

        setupToolbar(view.findViewById<Toolbar>(R.id.toolbar))
        runVersionCheck()
        return view
    }

    override fun onStart() {
        super.onStart()
        drawerContainer?.setDrawerLocked(false)
    }

    override fun onStop() {
        super.onStop()
        drawerContainer?.setDrawerLocked(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val savedState = ui.saveCurrentState()
        outState.putParcelable(SAVED_STATE_KEY, savedState)
    }

    private fun setupToolbar(toolbar: Toolbar?) {
        if (toolbar == null) return
        val activity = requireActivity() as AppCompatActivity

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun removeHistoryEntryAt(entryPosition: Int) {
        historyWidget.removeAt(entryPosition)
    }

    fun readHistoryEntries(): ArrayList<String> {
        return historyWidget.read()
    }

    fun inputQueryText(queryText: String) {
        ui.replaceQueryText(queryText)
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

    private fun runVersionCheck() {
        lifecycleScope.launch {
            VersionManager.runIfNewVersionAvailable { _ ->
                if (isActive) {
                    ui.showNewVersionNotification {
                        onNewVersionNotificationClicked()
                    }
                }
            }
        }
    }
}
