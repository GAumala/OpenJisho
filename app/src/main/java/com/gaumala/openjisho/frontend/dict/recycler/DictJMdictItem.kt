package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.databinding.DictJmdictItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class DictJMdictItem(
    val summarized: JMdictEntry.Summarized,
    val onClicked: () -> Unit
): BindableItem<DictJmdictItemBinding>(summarized.entry.entryId) {

    override fun bind(viewBinding: DictJmdictItemBinding, position: Int) {
        JMdictItem(summarized).bind(viewBinding.jmdictItem, position)

        viewBinding.card.setOnClickListener {
            onClicked()
        }
    }

    override fun getLayout() = R.layout.dict_jmdict_item

    override fun equals(other: Any?): Boolean {
        if (other !is DictJMdictItem)
            return false

        return other.summarized.entry.entryId == summarized.entry.entryId
    }

    override fun hashCode(): Int {
        return summarized.entry.entryId.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        DictJmdictItemBinding.bind(view)
}