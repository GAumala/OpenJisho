package com.gaumala.openjisho.frontend.study_list.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.ScTextItemBinding
import com.gaumala.openjisho.utils.ui.CardViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder

class SCTextItem(
    private val cardId: Long,
    private val text: String,
    private val onClicked: () -> Unit)
    : BindableItem<ScTextItemBinding>(), IdentifiableSCItem {

    override fun bind(viewBinding: ScTextItemBinding, position: Int) {
        viewBinding.headerText.text = text

        viewBinding.card.setOnClickListener { onClicked() }
    }

    override fun createViewHolder(itemView: View): GroupieViewHolder<ScTextItemBinding> {
        val viewDataBinding = initializeViewBinding(itemView)
        return CardViewHolder(viewDataBinding,
            viewDataBinding.rootLayout,
            viewDataBinding.card,
            viewDataBinding.deleteIcon)
    }

    override fun bind(
        viewHolder: GroupieViewHolder<ScTextItemBinding>,
        position: Int,
        payloads: MutableList<Any>) {
        bind(viewHolder.binding, position, payloads)

        val holder = viewHolder as? CardViewHolder<*> ?: return
        holder.clearView()
    }

    override fun getLayout() = R.layout.sc_text_item

    override fun getStudyCardId(): Long = cardId

    override fun isSameAs(other: Item<*>): Boolean {
        val otherItem = other as? IdentifiableSCItem ?: return false

        return otherItem.getStudyCardId() == getStudyCardId()
    }

    override fun equals(other: Any?): Boolean {
        val otherItem = other as? SCTextItem ?: return false
        return otherItem.text == text
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        ScTextItemBinding.bind(view)
}