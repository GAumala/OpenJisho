package com.gaumala.openjisho.frontend.dict

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.gaumala.openjisho.R
import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.BaseUI
import com.gaumala.openjisho.frontend.dict.actions.RunQuery
import com.xwray.groupie.GroupAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.viewpager.widget.ViewPager
import com.gaumala.openjisho.frontend.dict.actions.LoadMoreResults
import com.gaumala.openjisho.frontend.dict.recycler.DictItemFactory
import com.gaumala.openjisho.frontend.history.DictHistoryWidget
import com.gaumala.openjisho.frontend.navigation.NavDrawerContainer
import com.gaumala.openjisho.utils.hideKeyboard
import com.gaumala.openjisho.utils.openKeyboard
import com.gaumala.openjisho.utils.recycler.restoreState
import com.gaumala.openjisho.utils.recycler.saveState
import com.gaumala.openjisho.utils.recycler.setOnScrollToBottomListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.xwray.groupie.GroupieViewHolder

class DictUI(owner: LifecycleOwner,
             view: View,
             private val delayKeyboardBy: Long,
             private val itemFactory: DictItemFactory,
             private val historyWidget: DictHistoryWidget,
             private val drawerContainer: NavDrawerContainer?,
             private val dictClickHandler: DictClickHandler,
             private val sink: ActionSink<DictState, DictSideEffect>,
             liveState: LiveData<DictState>
): BaseUI<DictState>(owner, liveState) {

    private val ctx: Context = view.context
    private val searchEditText: EditText = view.findViewById(R.id.search_edit_text)
    private val fab: FloatingActionButton = view.findViewById(R.id.speed_dial_fab)
    private val drawerMenuButton: View? = view.findViewById(R.id.drawer_menu_icon)
    private val radicalsButton: View = view.findViewById(R.id.radical_search_icon)
    private val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
    private val viewPager: ViewPager = view.findViewById(R.id.pager)
    private val entriesRecycler: RecyclerView = view.findViewById(R.id.entries_recycler)
    private val sentencesRecycler: RecyclerView = view.findViewById(R.id.sentences_recycler)
    private val searchCompanionButton: ImageView = view.findViewById(R.id.search_companion_btn)
    private val entriesAdapter = GroupAdapter<GroupieViewHolder>()
    private val sentencesAdapter = GroupAdapter<GroupieViewHolder>()

    private val searchInputWatcher = object :TextWatcher {
        var isEnabled = true
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (!isEnabled)
                return

            val newText = s!!.toString()
            updateSearchCompanionButton(newText)

            if (newText.isEmpty())
                return

            sink.submitAction(RunQuery(
                queryText = newText,
                lookupSentences = viewPager.currentItem == 1,
                shouldThrottle = true))
        }
    }

    init {
        setupPager()
        setupRecyclerView()
        setupEditText()
        setupMenuButtons()

        openKeyboardAtStart()

        val recoveredState = liveState.value?.stateToRestore
        if (recoveredState != null)
            restoreSavedStateUI(recoveredState)
    }

    private fun restoreSavedStateUI(savedState: DictSavedState) {
        searchInputWatcher.isEnabled = false
        searchEditText.setText(savedState.queryText)
        updateSearchCompanionButton(savedState.queryText)
        searchInputWatcher.isEnabled = true

        viewPager.currentItem = savedState.selectedTab

        val entriesState = savedState.entriesState
        if (entriesState != null)
            entriesRecycler.restoreState(entriesState)

        val sentencesState = savedState.sentencesState
        if (sentencesState != null)
            sentencesRecycler.restoreState(sentencesState)
    }

    // Request focus to edit text after transition finishes.
    // Only do this once.
    private fun openKeyboardAtStart() {
        searchEditText.postDelayed({
            val currentState = liveState.value!!

            val hasNotBeenTouchedYet =
                currentState.entryResults is EntryResults.Welcome
                    && currentState.sentenceResults is SentenceResults.Welcome

            if (hasNotBeenTouchedYet)
                searchEditText.openKeyboard()

        }, delayKeyboardBy)
    }



    private fun setupEditText() {
        searchEditText.addTextChangedListener(searchInputWatcher)

        searchCompanionButton.setOnClickListener {
            searchEditText.setText("")
            searchEditText.openKeyboard()
        }

        updateSearchCompanionButton(searchEditText.text.toString())
    }

    private fun setupMenuButtons() {
        drawerMenuButton?.setOnClickListener { _ ->
            drawerContainer?.openDrawer()
        }

        radicalsButton.setOnClickListener {
            searchEditText.hideKeyboard()

            // Delay transition for 3~ frames to wait for keyboard to hide
            searchEditText.postDelayed({
                val savedState = getSavedState()
                dictClickHandler.onRadicalSearchButtonClicked(savedState)
            }, 48)
        }

        fab.setOnClickListener {
            searchEditText.hideKeyboard()

            it.postDelayed({
                historyWidget.open()
            }, 48)
        }
    }

    private fun updateSearchCompanionButton(searchText: String) {
        val newImageLevel = if (searchText.isEmpty()) 0 else 1
        searchCompanionButton.setImageLevel(newImageLevel)
    }

    private fun setupPager() {
        val adapter = DictPagerAdapter(entriesRecycler, sentencesRecycler)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab) {
                val queryText = searchEditText.text.toString()
                if (queryText.isEmpty())
                    return
                sink.submitAction(
                    RunQuery(
                        queryText = queryText,
                        lookupSentences = p0.position == 1,
                        shouldThrottle = false))
            }

        })
    }

    private fun setupRecyclerView() {
        entriesRecycler.adapter = entriesAdapter
        sentencesRecycler.adapter = sentencesAdapter

        val dividerItemDecoration = DividerItemDecoration(
            entriesRecycler.context,
            RecyclerView.VERTICAL
        )
        val dividerDrawable = ContextCompat.getDrawable(ctx, R.drawable.list_divider)!!
        dividerItemDecoration.setDrawable(dividerDrawable)
        entriesRecycler.addItemDecoration(dividerItemDecoration)
        sentencesRecycler.addItemDecoration(dividerItemDecoration)

        entriesRecycler.setOnScrollToBottomListener {
            sink.submitAction(LoadMoreResults(false))
        }
        sentencesRecycler.setOnScrollToBottomListener {
            sink.submitAction(LoadMoreResults(true))
        }
    }

    /*
     * We want to call this everytime rebind() runs so that the
     * history is always up to date. But before updating, we want
     * to make sure that the results we are showing match the
     * current query.
     */
    private fun pushResultsToHistory(
        entryResults: EntryResults, sentenceResults: SentenceResults
    ) {
        val currentQueryText = searchEditText.text.toString()
        val hasEntryResultsForCurrentQuery =
            entryResults is EntryResults.Ready
                && entryResults.queryText == currentQueryText
        val hasSentenceResultsForCurrentQuery =
            sentenceResults is SentenceResults.Ready
                && sentenceResults.queryText == currentQueryText

        if (hasEntryResultsForCurrentQuery
            || hasSentenceResultsForCurrentQuery) {
            historyWidget.push(currentQueryText)
        }
    }

    override fun rebind(state: DictState) {
        entriesAdapter.update(
            itemFactory.fromEntryResults(state.entryResults)
        )
        sentencesAdapter.update(
            itemFactory.fromSentenceResults(state.sentenceResults)
        )
        pushResultsToHistory(state.entryResults, state.sentenceResults)
    }

    fun replaceQueryText(queryText: String) {
        if (queryText.isEmpty())
            return

        // I'm disabling the text watcher because I want to
        // handle this a bit differently. After replacing text,
        // search should be immediate, no throttling because input
        // isn't manual
        searchInputWatcher.isEnabled = false
        searchEditText.apply {
            // These two steps are important when replacing
            // so that the cursor stays at the end of the text
            setText("")
            append(queryText)
        }
        searchInputWatcher.isEnabled = true

        updateSearchCompanionButton(queryText)
        sink.submitAction(RunQuery(
            queryText,
            viewPager.currentItem == 1,
            shouldThrottle = false))
    }

    fun getSavedState(): DictSavedState? {
        val latestQueryText = searchEditText.text.toString()
        return liveState.value?.toSavedState(
            queryText = latestQueryText,
            selectedTab = viewPager.currentItem,
            entriesState = entriesRecycler.saveState(),
            sentencesState = sentencesRecycler.saveState())
    }
}