package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.databinding.KanjidicItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class KanjidicItem(private val entry: KanjidicEntry)
    : BindableItem<KanjidicItemBinding>() {

    override fun bind(viewBinding: KanjidicItemBinding, position: Int) {
        viewBinding.headerText.text = entry.literal
        viewBinding.kunText.text = entry.kunReadings.joinToString(separator = ", ")
        viewBinding.onText.text = entry.onReadings.joinToString(separator = ", ")
        viewBinding.subText.text = entry.meanings.joinToString(separator = ", ")
    }

    override fun getLayout() = R.layout.kanjidic_item

    override fun equals(other: Any?): Boolean {
        if (other !is KanjidicItem)
            return false

        return other.entry == entry
    }

    override fun hashCode(): Int {
        return entry.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        KanjidicItemBinding.bind(view)

}