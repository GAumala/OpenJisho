package com.gaumala.openjisho.frontend.radicals

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.radicals.actions.AppendKanji
import com.gaumala.openjisho.frontend.radicals.actions.DeleteKanji
import com.gaumala.openjisho.frontend.radicals.actions.ToggleRadical
import com.gaumala.openjisho.frontend.radicals.recycler.RadicalsItemFactory
import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.BaseUI
import com.gaumala.openjisho.utils.image.MatrixCalculator
import com.gaumala.openjisho.utils.image.MatrixImageView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class RadicalsUI(
    owner: LifecycleOwner,
    view: View,
    private val sink: ActionSink<RadicalsState, RadicalsSideEffect>,
    private val returnToDict: (String) -> Unit,
    liveState: LiveData<RadicalsState>
    ): BaseUI<RadicalsState>(owner, liveState) {

    private val ctx: Context = view.context
    private val dictButton: View = view.findViewById(R.id.dict_search_icon)
    private val searchInputView: View = view.findViewById(R.id.search_input)
    private val searchEditText: EditText = view.findViewById(R.id.search_edit_text)
    private val radicalsRecycler: RecyclerView = view.findViewById(R.id.radicals_recycler)
    private val resultsRecycler: RecyclerView = view.findViewById(R.id.results_recycler)
    private val searchCompanionButton: ImageView = view.findViewById(R.id.search_companion_btn)
    private val selectedRadicalsGroup: ChipGroup = view.findViewById(R.id.selected_radicals_group)
    private val welcomeGroup: View = view.findViewById(R.id.welcome_group)
    private val resultsGroup: View = view.findViewById(R.id.results_group)
    private val radicalsAdapter = GroupAdapter<GroupieViewHolder>()
    private val resultsAdapter = GroupAdapter<GroupieViewHolder>()

    private val onRadicalSelected: (RadicalIndex) -> Unit = {
        sink.submitAction(ToggleRadical(it))
    }
    private val onKanjiSelected: (String) -> Unit = {
        sink.submitAction(AppendKanji(it))
    }
    private val itemFactory =
        RadicalsItemFactory(onRadicalSelected, onKanjiSelected)

    private var lastRadicalList: List<RadicalIndex>? = null
    private var lastKanjiResults: KanjiResults? = null

    init {
        setupRecyclerView()
        setupArt(view)
    }

    private fun setupArt(view: View) {
        val welcomeArtView: MatrixImageView = view.findViewById(R.id.welcome_art)
        welcomeArtView.matrixCalculator = MatrixCalculator.FitTop()
    }

    private fun countMaxColumns(columnWidthRes: Int): Int {
        val displayMetrics = ctx.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val columnWidth = ctx.resources.getDimension(columnWidthRes)
        return (screenWidth / columnWidth).toInt()
    }

    private fun setupRecyclerView() {
        radicalsRecycler.layoutManager =
            GridLayoutManager(ctx, countMaxColumns(R.dimen.radical_grid_size))
        radicalsRecycler.adapter = radicalsAdapter
        resultsRecycler.layoutManager =
            GridLayoutManager(ctx, countMaxColumns(R.dimen.kanji_grid_size))
        resultsRecycler.adapter = resultsAdapter
    }

    private fun updateResultGridColumns(kanjiResults: KanjiResults) {
        val glm = resultsRecycler.layoutManager as GridLayoutManager
        glm.spanCount =
            if (kanjiResults is KanjiResults.Ready)
                countMaxColumns(R.dimen.kanji_grid_size)
            else 1
    }

    private fun updateSearchCompanionButton(searchText: String) {
        val newImageLevel = if (searchText.isEmpty()) 0 else 1
        searchCompanionButton.setImageLevel(newImageLevel)

        searchCompanionButton.setOnClickListener(
            if (searchText.isEmpty()) null
            else View.OnClickListener{
                sink.submitAction(DeleteKanji())
            })
    }

    private fun rebindQueryText(queryText: String) {
        val currentText = searchEditText.text.toString()
        if (currentText != queryText) {
            searchEditText.setText("")
            if (queryText.isNotEmpty())
                searchEditText.append(queryText)

            updateSearchCompanionButton(queryText)
        }
    }

    private fun createRadicalChip(radical: RadicalIndex): Chip {
        val chip = Chip(ctx)
        chip.text = radical.unicodeChar
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            sink.submitAction(ToggleRadical(radical))
        }
        return chip
    }

    private fun rebindSelectedRadicals(newRadicals: List<RadicalIndex>) {
        selectedRadicalsGroup.removeAllViews()

        val selectedRadicals = newRadicals
            .filter { it.buttonState == RadicalButtonState.selected }

        if (selectedRadicals.isEmpty()) {
            resultsGroup.visibility = View.INVISIBLE
            welcomeGroup.visibility = View.VISIBLE
        } else {
            resultsGroup.visibility = View.VISIBLE
            welcomeGroup.visibility = View.INVISIBLE
        }

        selectedRadicals.forEach { radical ->
            val chip = createRadicalChip(radical)
            selectedRadicalsGroup.addView(chip)
        }
    }

    override fun rebind(state: RadicalsState) {
        dictButton.setOnClickListener { returnToDict(state.queryText) }
        searchInputView.setOnClickListener { returnToDict(state.queryText) }

        rebindQueryText(state.queryText)

        if (lastRadicalList !== state.radicals) {
            rebindSelectedRadicals(state.radicals)
            radicalsAdapter.update(itemFactory.generateRadicals(state.radicals))
            lastRadicalList = state.radicals
        }
        if (lastKanjiResults !== state.results) {
            updateResultGridColumns(state.results)
            resultsAdapter.update(itemFactory.generateResults(state.results))
            lastKanjiResults = state.results
        }
    }

    fun onBackPressed() {
        val state = liveState.value!!
        returnToDict(state.queryText)
    }
}