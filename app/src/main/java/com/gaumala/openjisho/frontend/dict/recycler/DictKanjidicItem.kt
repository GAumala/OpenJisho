package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.databinding.DictKanjidicItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class DictKanjidicItem(
    private val entry: KanjidicEntry,
    private val onClicked: () -> Unit
): BindableItem<DictKanjidicItemBinding>(entry.literal.hashCode().toLong()) {

    override fun bind(viewBinding: DictKanjidicItemBinding, position: Int) {
        KanjidicItem(entry).bind(viewBinding.kanjidicItem, position)

        viewBinding.card.setOnClickListener {
            onClicked()
        }
    }

    override fun getLayout() = R.layout.dict_kanjidic_item

    override fun equals(other: Any?): Boolean {
        val otherItem =  other as? DictKanjidicItem ?: return false
        return otherItem.entry.literal == entry.literal
    }

    override fun hashCode(): Int {
        return entry.literal.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        DictKanjidicItemBinding.bind(view)
}