package com.gaumala.openjisho.frontend.study_list.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.databinding.ScJmdictItemBinding
import com.gaumala.openjisho.frontend.dict.recycler.JMdictItem
import com.gaumala.openjisho.utils.ui.CardViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder

class SCJMdictItem(
    private val cardId: Long,
    private val summarized: JMdictEntry.Summarized,
    private val onClicked: () -> Unit
): BindableItem<ScJmdictItemBinding>(cardId), IdentifiableSCItem {

    override fun bind(viewBinding: ScJmdictItemBinding, position: Int) {
        val jmdictBinding = viewBinding.jmdictItem

        val jmdictItem = JMdictItem(summarized)
        jmdictItem.bind(jmdictBinding, position)

        viewBinding.card.setOnClickListener { onClicked() }
    }

    override fun createViewHolder(itemView: View): GroupieViewHolder<ScJmdictItemBinding> {
        val viewDataBinding = initializeViewBinding(itemView)
        return CardViewHolder(viewDataBinding,
            viewDataBinding.rootLayout,
            viewDataBinding.card,
            viewDataBinding.deleteIcon)
    }

    override fun bind(
        viewHolder: GroupieViewHolder<ScJmdictItemBinding>,
        position: Int,
        payloads: MutableList<Any>) {
        bind(viewHolder.binding, position, payloads)

        val holder = viewHolder as? CardViewHolder<*> ?: return
        holder.clearView()

    }

    override fun getLayout() = R.layout.sc_jmdict_item

    override fun getStudyCardId(): Long = cardId

    override fun equals(other: Any?): Boolean {
        val otherItem = other as? SCJMdictItem ?: return false

        return otherItem.summarized.entry.entryId == summarized.entry.entryId
    }

    override fun isSameAs(other: Item<*>): Boolean {
        val otherItem = other as? IdentifiableSCItem ?: return false

        return otherItem.getStudyCardId() == getStudyCardId()
    }

    override fun hashCode(): Int {
        return summarized.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        ScJmdictItemBinding.bind(view)
}