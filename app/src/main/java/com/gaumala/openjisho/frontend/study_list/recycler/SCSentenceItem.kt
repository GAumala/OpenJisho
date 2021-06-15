package com.gaumala.openjisho.frontend.study_list.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.ScSentenceItemBinding
import com.gaumala.openjisho.utils.ui.CardViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder

class SCSentenceItem(
    private val cardId: Long,
    private val japanese: String,
    private val english: String,
    private val onClicked: () -> Unit)
    : BindableItem<ScSentenceItemBinding>(), IdentifiableSCItem {

    override fun bind(viewBinding: ScSentenceItemBinding, position: Int) {
        val binding = viewBinding.sentenceItem
        binding.headerText.text = japanese
        binding.subText.text = english

        viewBinding.card.setOnClickListener { onClicked() }
    }

    override fun createViewHolder(itemView: View): GroupieViewHolder<ScSentenceItemBinding> {
        val viewDataBinding = initializeViewBinding(itemView)
        return CardViewHolder(viewDataBinding,
            viewDataBinding.rootLayout,
            viewDataBinding.card,
            viewDataBinding.deleteIcon)
    }

    override fun bind(
        viewHolder: GroupieViewHolder<ScSentenceItemBinding>,
        position: Int,
        payloads: MutableList<Any>) {
        bind(viewHolder.binding, position, payloads)

        val holder = viewHolder as? CardViewHolder<*> ?: return
        holder.clearView()
    }

    override fun getLayout() = R.layout.sc_sentence_item

    override fun getStudyCardId(): Long = cardId

    override fun isSameAs(other: Item<*>): Boolean {
        val otherItem = other as? IdentifiableSCItem ?: return false

        return otherItem.getStudyCardId() == getStudyCardId()
    }

    override fun equals(other: Any?): Boolean {
        val otherItem = other as? SCSentenceItem ?: return false
        return otherItem.japanese == japanese
                && otherItem.english == english
    }

    override fun hashCode(): Int {
        return japanese.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        ScSentenceItemBinding.bind(view)
}