package com.gaumala.openjisho.frontend.my_lists.recycler

import android.view.View
import androidx.core.content.ContextCompat
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.ListMetadataItemBinding
import com.gaumala.openjisho.frontend.my_lists.ListMetadata
import com.gaumala.openjisho.utils.ui.CardViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder

class ListMetadataItem(val metadata: ListMetadata,
                       private val isSelected: Boolean = false,
                       private val onClicked: () -> Unit,
                       private val onLongClicked: (() -> Unit)? = null)
    : BindableItem<ListMetadataItemBinding>() {

    override fun bind(viewBinding: ListMetadataItemBinding, position: Int) {
        viewBinding.headerText.text = metadata.name

        val cardColorResId =
            if (isSelected) R.color.selected_card_bg
            else android.R.color.white
        val cardColor = ContextCompat.getColor(
            viewBinding.root.context, cardColorResId)
        viewBinding.card.setBackgroundColor(cardColor)
        viewBinding.card.isSelected = isSelected

        viewBinding.card.setOnClickListener { onClicked() }

        val onLongClicked = this.onLongClicked
        if (onLongClicked != null)
            viewBinding.card.setOnLongClickListener {
                onLongClicked()
                true
            }
        else viewBinding.card.setOnLongClickListener(null)
    }


    override fun bind(
        viewHolder: GroupieViewHolder<ListMetadataItemBinding>,
        position: Int,
        payloads: MutableList<Any>) {
        bind(viewHolder.binding, position, payloads)

        val holder = viewHolder as? CardViewHolder<*> ?: return
        holder.clearView()

    }

    override fun getLayout() = R.layout.list_metadata_item

    override fun equals(other: Any?): Boolean {
        val otherItem = other as? ListMetadataItem
            ?: return false

        return otherItem.metadata == metadata
    }

    override fun hashCode(): Int {
        return metadata.hashCode()
    }

    override fun createViewHolder(itemView: View): GroupieViewHolder<ListMetadataItemBinding> {
        val binding = initializeViewBinding(itemView)
        return CardViewHolder(
            binding,
            binding.rootLayout,
            binding.card,
            binding.deleteIcon)
    }

    override fun initializeViewBinding(view: View): ListMetadataItemBinding {
        return ListMetadataItemBinding.bind(view)

    }
}