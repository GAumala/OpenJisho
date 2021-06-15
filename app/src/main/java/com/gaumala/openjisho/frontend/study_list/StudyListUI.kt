package com.gaumala.openjisho.frontend.study_list

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.BaseUI
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.*
import com.gaumala.openjisho.frontend.study_list.actions.*
import com.gaumala.openjisho.frontend.study_list.recycler.StudyListItemFactory
import com.gaumala.openjisho.utils.ui.AbstractSnackbar
import com.gaumala.openjisho.utils.fab_speed_dial.FABAction
import com.gaumala.openjisho.utils.fab_speed_dial.FABSpeedDial
import com.gaumala.openjisho.utils.fab_speed_dial.OnFABActionClickedListener
import com.gaumala.openjisho.utils.recycler.SpacingItemDecorator
import com.gaumala.openjisho.utils.ui.MovableItemTouchHelper
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class StudyListUI(owner: LifecycleOwner,
                  view: View,
                  private val sink: ActionSink<StudyListState, StudyListSideEffect>,
                  private val snackBar: AbstractSnackbar,
                  private val navigator: StudyListNavigator,
                  liveState: LiveData<StudyListState>)
    : BaseUI<StudyListState>(owner, liveState) {
    private val ctx = view.context
    private val speedDial: FABSpeedDial = view.findViewById(R.id.speed_dial)
    private val recycler: RecyclerView = view.findViewById(R.id.recycler)
    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val onFABActionClickedListener = object: OnFABActionClickedListener {
        override fun onFABActionClicked(action: FABAction) {
            when (action) {
                newTextFABAction -> navigator.gotToComposeText()
                newEntryFABAction -> navigator.goToPickDictEntry(true)
                newSentenceFABAction -> navigator.goToPickDictEntry(false)
            }
        }
    }

    private val touchHelper = object: MovableItemTouchHelper() {
        override fun onItemSwiped(itemPosition: Int) {
            sink.submitAction(DeleteCard(itemPosition))
        }

        override fun onItemMoved(srcPosition: Int, dstPosition: Int) {
            sink.submitAction(MoveCard(srcPosition, dstPosition))
        }
    }

    private val onItemClickedListener = { card: StudyCard ->
        when (card) {
            is StudyCard.JMdict ->
                navigator.goToJMdictEntry(card.summarized)
            is StudyCard.Kanjidic ->
                navigator.goToKanjidicEntry(card.entry)
            is StudyCard.Sentence ->
                navigator.goToSentence(card.japanese, card.english)
            is StudyCard.Text ->
                navigator.goToTextDetail(card.text)
        }
    }

    private val factory = StudyListItemFactory(onItemClickedListener)

    init {
        setupRecyclerView()
        speedDial.addItems(
            listOf(
                newEntryFABAction,
                newSentenceFABAction,
                newTextFABAction),
            onFABActionClickedListener
        )
    }

    private fun setupRecyclerView() {
        recycler.adapter = adapter

        val decoration =
            SpacingItemDecorator.fromDimen(ctx, R.dimen.cards_spacing)
        recycler.addItemDecoration(decoration)
        touchHelper.attachTo(recycler)
    }

    fun addTextItem(id: Long, text: String) {
        sink.submitAction(AddText(id, text))
    }

    fun addDictItem(id: Long,
                    resultJMdictEntry: JMdictEntry.Summarized) {
        sink.submitAction(AddJMdictEntry(id, resultJMdictEntry))
    }

    fun addDictItem(id: Long,
                    resultKanjidicEntry: KanjidicEntry) {
        sink.submitAction(AddKanjidicEntry(id, resultKanjidicEntry))
    }

    fun addSentenceItem(id: Long, resultSentence: Sentence) {
        sink.submitAction(AddSentence(id, resultSentence))
    }


    override fun rebind(state: StudyListState) {
        val cards = state.cards
        adapter.update(factory.create(cards))

        if (state.snackbarMsg != null)
            showSnackbarMsg(state.snackbarMsg)
    }

    private fun showSnackbarMsg(snackbarMsg: StudyListMsg) {
        when (snackbarMsg) {
            StudyListMsg.itemRemoved ->
                snackBar.show(
                    message = UIText.Resource(R.string.item_removed),
                    action = UIText.Resource(R.string.undo))
                { undo -> sink.submitAction(DismissSnackbar(undo)) }

            StudyListMsg.listUpdated ->
                snackBar.show(
                    message = UIText.Resource(R.string.list_updated),
                    action = UIText.Resource(R.string.undo))
                    { undo -> sink.submitAction(DismissSnackbar(undo)) }

            StudyListMsg.noItemsSelected ->
                snackBar.show(
                    message = UIText.Resource(R.string.no_items_selected))
                    { sink.submitAction(DismissSnackbar(false)) }
        }
    }

    companion object {
        val newEntryFABAction = FABAction(
            id = R.id.new_entry_action,
            textResId = R.string.dict_entry,
            iconResId = R.drawable.ic_search_white_17dp)
        val newSentenceFABAction = FABAction(
            id = R.id.new_sentence_action,
            textResId = R.string.example_sentence,
            iconResId = R.drawable.ic_search_white_17dp)
        val newTextFABAction = FABAction(
            id = R.id.new_text_action,
            textResId = R.string.free_text,
            iconResId = R.drawable.ic_edit_white_17dp)
    }
}