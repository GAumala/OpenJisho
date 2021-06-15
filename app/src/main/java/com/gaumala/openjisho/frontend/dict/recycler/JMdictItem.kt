package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.databinding.JmdictItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class JMdictItem(private val summarized: JMdictEntry.Summarized)
    : BindableItem<JmdictItemBinding>(summarized.entry.entryId) {

    override fun bind(viewBinding: JmdictItemBinding, position: Int) {
        val paint = viewBinding.headerText.paint
        viewBinding.headerFurigana.setText(paint, summarized.itemHeader)

        viewBinding.subText.text = summarized.sub
    }

    override fun getLayout() = R.layout.jmdict_item

    override fun initializeViewBinding(view: View): JmdictItemBinding {
        return JmdictItemBinding.bind(view)
    }
}