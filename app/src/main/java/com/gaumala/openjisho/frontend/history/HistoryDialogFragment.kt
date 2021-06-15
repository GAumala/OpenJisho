package com.gaumala.openjisho.frontend.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.dict.recycler.HistoryItem
import com.gaumala.openjisho.utils.ui.SwipeableItemTouchHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem

/**
 * A dialog fragment that displays the user's search history.
 *
 * This is displayed after the user clicks the history floating action button
 * in [DictFragment].
 */
class HistoryDialogFragment: BottomSheetDialogFragment() {

    private val mainFragment: DictFragment?
        get() {
            val manager = activity!!.supportFragmentManager
            return manager.findFragmentById(R.id.container) as? DictFragment
        }

    private val historyEntries: List<BindableItem<*>>
        get() = mainFragment?.readHistoryEntries()?.map {
            HistoryItem(it) { chooseItem(it) }
        } ?: emptyList()

    val adapter = GroupAdapter<GroupieViewHolder>()

    private val touchHelper = object: SwipeableItemTouchHelper() {
        override fun onItemSwiped(itemPosition: Int) {
            mainFragment?.removeHistoryEntryAt(itemPosition)
            adapter.update(historyEntries)
        }
    }

    private fun chooseItem(item: String) {
        dismiss()
        mainFragment?.inputQueryText(item)
    }


    private fun bindView(view: View) {
        val recyclerView =
            view.findViewById<RecyclerView>(R.id.history_recycler)
        recyclerView.adapter = adapter
        adapter.update(historyEntries)
        touchHelper.attachTo(recyclerView)

        val closeButton =
            view.findViewById<View>(R.id.close_btn)
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.history_widget, null)
        bindView(view)
        return view
    }

    companion object {
        fun create(): HistoryDialogFragment {
            return HistoryDialogFragment()
        }
    }
}