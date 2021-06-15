package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.HistoryItemBinding
import com.gaumala.openjisho.utils.ui.CardViewHolder
import com.gaumala.openjisho.utils.ui.SwipeableContainer
import com.gaumala.openjisho.utils.ui.SwipeableViewHolder
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder

class HistoryItem(private val entry: String,
                  private val onItemSelected: () -> Unit)
    : BindableItem<HistoryItemBinding>(entry.hashCode().toLong()) {

    override fun bind(viewBinding: HistoryItemBinding, position: Int) {
        viewBinding.textView.text = entry
        viewBinding.root.setOnClickListener { onItemSelected() }

    }

    override fun bind(
        viewHolder: GroupieViewHolder<HistoryItemBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        bind(viewHolder.binding, position)

        val swipeable = viewHolder as? SwipeableContainer ?: return
        swipeable.clearView()
    }

    override fun getLayout() = R.layout.history_item

    override fun equals(other: Any?): Boolean {
        if (other !is HistoryItem)
            return false

        return other.entry == entry
    }

    override fun hashCode(): Int {
        return entry.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        HistoryItemBinding.bind(view)

    override fun createViewHolder(itemView: View): GroupieViewHolder<HistoryItemBinding> {
        val binding = initializeViewBinding(itemView)
        return SwipeableViewHolder(
            binding,
            binding.root,
            binding.textView,
            binding.deleteIcon)
    }
}
