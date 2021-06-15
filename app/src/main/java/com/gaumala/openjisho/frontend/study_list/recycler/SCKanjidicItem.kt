package com.gaumala.openjisho.frontend.study_list.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.databinding.ScKanjidicItemBinding
import com.gaumala.openjisho.frontend.dict.recycler.KanjidicItem
import com.gaumala.openjisho.utils.ui.CardViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder

class SCKanjidicItem(
    private val cardId: Long,
    private val entry: KanjidicEntry,
    private val onClicked: () -> Unit
): BindableItem<ScKanjidicItemBinding>(), IdentifiableSCItem {

    override fun bind(viewBinding: ScKanjidicItemBinding, position: Int) {
        val kanjidicBinding = viewBinding.kanjidicItem

        val kanjidicItem = KanjidicItem(entry)
        kanjidicItem.bind(kanjidicBinding, position)

        viewBinding.card.setOnClickListener { onClicked() }
    }

    override fun createViewHolder(itemView: View): GroupieViewHolder<ScKanjidicItemBinding> {
        val viewDataBinding = initializeViewBinding(itemView)
        return CardViewHolder(viewDataBinding,
            viewDataBinding.rootLayout,
            viewDataBinding.card,
            viewDataBinding.deleteIcon)
    }

    override fun bind(
        viewHolder: GroupieViewHolder<ScKanjidicItemBinding>,
        position: Int,
        payloads: MutableList<Any>) {
        bind(viewHolder.binding, position, payloads)

        val holder = viewHolder as? CardViewHolder<*> ?: return
        holder.clearView()
    }

    override fun getStudyCardId(): Long = cardId

    override fun isSameAs(other: Item<*>): Boolean {
        val otherItem = other as? IdentifiableSCItem ?: return false

        return otherItem.getStudyCardId() == getStudyCardId()
    }

    override fun getLayout() = R.layout.sc_kanjidic_item

    override fun equals(other: Any?): Boolean {
        val otherItem =  other as? SCKanjidicItem ?: return false
        return otherItem.entry.literal == entry.literal
    }

    override fun hashCode(): Int {
        return entry.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        ScKanjidicItemBinding.bind(view)
}