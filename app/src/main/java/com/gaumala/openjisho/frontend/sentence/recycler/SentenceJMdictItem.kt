package com.gaumala.openjisho.frontend.sentence.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.databinding.JmdictItemBinding
import com.gaumala.openjisho.frontend.dict.recycler.JMdictItem
import com.xwray.groupie.viewbinding.BindableItem

class SentenceJMdictItem(
    val summarized: JMdictEntry.Summarized,
    val position: Int,
    val onClicked: (JMdictEntry.Summarized) -> Unit
): BindableItem<JmdictItemBinding>(position.toLong()) {

    override fun bind(viewBinding: JmdictItemBinding, position: Int) {
        JMdictItem(summarized).bind(viewBinding, position)

        viewBinding.root.setOnClickListener {
            onClicked(summarized)
        }
    }

    override fun getLayout() = R.layout.jmdict_item

    override fun equals(other: Any?): Boolean {
        if (other !is SentenceJMdictItem)
            return false

        return other.summarized.entry.entryId == summarized.entry.entryId
    }

    override fun hashCode(): Int {
        return position
    }

    override fun initializeViewBinding(view: View) =
        JmdictItemBinding.bind(view)
}