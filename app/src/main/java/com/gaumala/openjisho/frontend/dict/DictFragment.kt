package com.gaumala.openjisho.frontend.dict

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gaumala.openjisho.R
import com.gaumala.openjisho.backend.lists.ListsDao
import com.gaumala.openjisho.frontend.dict.recycler.DictItemFactory
import com.gaumala.openjisho.frontend.history.DictHistoryWidget
import com.gaumala.openjisho.frontend.my_lists.MyListsCache
import com.gaumala.openjisho.frontend.navigation.NavDrawerContainer
import com.gaumala.openjisho.utils.SystemUIHelper
import com.gaumala.openjisho.utils.async.CoroutineIOWorker

/**
 * The main fragment of the app. Here is where the user can
 * lookup words or sentences.
 */
class DictFragment : Fragment() {

    companion object {
        fun newInstance(delayKeyboardBy: Long = 0,
                        savedState: DictSavedState? = null,
                        isPicker: Boolean = false): DictFragment {
            val args = Bundle()
            args.putLong(DELAY_KEYBOARD_BY_KEY, delayKeyboardBy)
            args.putParcelable(SAVED_STATE_KEY, savedState)
            args.putBoolean(IS_PICKER_KEY, isPicker)

            val f = DictFragment()
            f.arguments = args
            return f
        }


        const val DELAY_KEYBOARD_BY_KEY = "delayKeyboardBy"
        const val SAVED_STATE_KEY = "dictSavedState"
        const val IS_PICKER_KEY = "isPicker"

        const val SEARCH_INTERVAL = 800L
    }

    private lateinit var ui: DictUI

    private val delayKeyboardBy by lazy {
        arguments!!.getLong(DELAY_KEYBOARD_BY_KEY)
    }

    private val isPicker: Boolean by lazy {
        arguments!!.getBoolean(IS_PICKER_KEY)
    }

    private val layout by lazy {
        if (isPicker)
            R.layout.pick_dict_item_fragment
        else
            R.layout.dict_fragment
    }


    private val drawerContainer by lazy {
        requireActivity() as? NavDrawerContainer
    }

    private val dictClickHandler by lazy {
        if (isPicker)
            DictClickHandler.Picker(this)
        else
            DictClickHandler.Default(this)
    }

    private val historyWidget by lazy {
        DictHistoryWidget(this)
    }

    private val itemFactory by lazy {
        DictItemFactory(
            isPicker = isPicker,
            pushToHistory = { historyWidget.push(it) },
            onJMdictEntryClicked = { entry ->
                dictClickHandler.onJMdictEntryClicked(entry)
            },
            onKanjidicEntryClicked = {entry ->
                dictClickHandler.onKanjidicEntryClicked(entry)
            },
            onSentenceClicked = { sentence ->
                dictClickHandler.onSentenceClicked(sentence)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val factory = DictViewModelFactory(this, savedInstanceState)
        val viewModel =
            ViewModelProvider(this, factory)
                .get(DictViewModel::class.java)

        val view = inflater.inflate(
            layout, container, false)

        ui = DictUI(owner = this.viewLifecycleOwner,
            delayKeyboardBy = delayKeyboardBy,
            itemFactory = itemFactory,
            dictClickHandler = dictClickHandler,
            view = view,
            historyWidget = historyWidget,
            sink = viewModel.userActionSink,
            drawerContainer = requireActivity() as? NavDrawerContainer,
            liveState = viewModel.liveState)
        ui.subscribe()

        SystemUIHelper(this).matchWithPrimary()

        setupToolbar(view.findViewById<Toolbar>(R.id.toolbar))

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                requireActivity().onBackPressed()
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        drawerContainer?.setDrawerLocked(false)
        preloadMyLists()
    }

    override fun onStop() {
        super.onStop()
        drawerContainer?.setDrawerLocked(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val savedState = ui.getSavedState()
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

    fun getSavedState(): DictSavedState? {
        return ui.getSavedState()
    }

    /**
     * load lists metadata here, so that when the user navigates to
     * My Lists, the data is already cached.
     */
    private fun preloadMyLists() {
        val ctx = requireContext()
        val worker = CoroutineIOWorker(lifecycleScope)
        val dao = ListsDao.Default(ctx)

        val cache = MyListsCache.Default(worker, dao)
        cache.preload()
    }
}
