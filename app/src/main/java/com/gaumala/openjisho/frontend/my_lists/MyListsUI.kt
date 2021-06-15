package com.gaumala.openjisho.frontend.my_lists

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.BaseUI
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.*
import com.gaumala.openjisho.frontend.my_lists.actions.*
import com.gaumala.openjisho.frontend.my_lists.recycler.ListMetadataItemFactory
import com.gaumala.openjisho.utils.recycler.SpacingItemDecorator
import com.gaumala.openjisho.utils.ui.AbstractSnackbar
import com.gaumala.openjisho.utils.ui.ContextualToolbarMenu
import com.gaumala.openjisho.utils.ui.SwipeableItemTouchHelper
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class MyListsUI(owner: LifecycleOwner,
                view: View,
                navigator: MyListsNavigator,
                private val sink: ActionSink<MyListsState, MyListsSideEffect>,
                private val toolbarMenu: ContextualToolbarMenu,
                private val snackBar: AbstractSnackbar,
                liveState: LiveData<MyListsState>
)
    : BaseUI<MyListsState>(owner, liveState) {
    private val ctx = view.context
    private val newListButton: View = view.findViewById(R.id.new_list_btn)
    private val recycler: RecyclerView = view.findViewById(R.id.recycler)
    private val adapter = GroupAdapter<GroupieViewHolder>()

    // Wrap navigator to always dismiss snackbar before leaving
    private val wrappedNavigator = object: MyListsNavigator {
        override fun goToList(listName: String) {
            sink.submitAction(DismissSnackbar(false))
            navigator.goToList(listName)
        }

        override fun goToNewListForm() {
            sink.submitAction(DismissSnackbar(false))
            navigator.goToNewListForm()
        }

    }
    private val touchHelper = object: SwipeableItemTouchHelper() {
        override fun onItemSwiped(itemPosition: Int) {
            sink.submitAction(DeleteList(itemPosition))
        }
    }

    private val onSelectableItemClickedListener = { listName: String ->
        sink.submitAction(ToggleSelection(listName))
    }

    private val onItemLongClickedListener = { listName: String ->
        sink.submitAction(StartSelection(listName))
    }

    private val onMenuItemClickedListener = { id: Int ->
        when (id) {
            R.id.action_delete -> sink.submitAction(DeleteSelection())
        }
    }

    private val onMenuClosedListener = { ->
        sink.submitAction(CancelSelection())
    }

    private val factory = ListMetadataItemFactory(
        onItemClicked = { wrappedNavigator.goToList(it.name) },
        onSelectableItemClicked = onSelectableItemClickedListener,
        onItemLongClicked = onItemLongClickedListener)

    init {
        setupRecyclerView()
        toolbarMenu.setItemClickedListener(onMenuItemClickedListener)
        toolbarMenu.setOnCloseListener(onMenuClosedListener)
        newListButton.setOnClickListener { wrappedNavigator.goToNewListForm() }
    }

    private fun setupRecyclerView() {
        recycler.adapter = adapter

        val decoration =
            SpacingItemDecorator.fromDimen(ctx, R.dimen.cards_spacing)
        recycler.addItemDecoration(decoration)
    }

    override fun rebind(state: MyListsState) {
        val lists = state.lists
        adapter.update(factory.create(lists))

        when (lists) {
            is LoadedLists.Ready -> {
                toolbarMenu.hide()
                if (lists.list.isEmpty())
                    touchHelper.detach()
                else
                    touchHelper.attachTo(recycler)
            }
            is LoadedLists.MultiSelect -> toolbarMenu.show()
            else -> {
                touchHelper.detach()
                toolbarMenu.hide()
            }
        }

        if (state.snackbarMsg != null)
            showSnackbarMsg(state.snackbarMsg)
    }

    private fun showSnackbarMsg(snackbarMsg: MyListsMsg) {
        when (snackbarMsg) {
            MyListsMsg.listDeleted ->
                snackBar.show(
                    message = UIText.Resource(R.string.list_deleted),
                    action = UIText.Resource(R.string.undo))
                { undo -> sink.submitAction(DismissSnackbar(undo)) }

            MyListsMsg.selectionDeleted ->
                snackBar.show(
                    message = UIText.Resource(R.string.selection_deleted),
                    action = UIText.Resource(R.string.undo))
                { undo -> sink.submitAction(DismissSnackbar(undo)) }
        }
    }
}