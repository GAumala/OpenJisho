package com.gaumala.openjisho.frontend.radicals.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.ResultKanjiItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class ResultKanjiItem(private val kanji: String,
                      private val onKanjiSelected: (String) -> Unit)
    : BindableItem<ResultKanjiItemBinding>(
        kanji.codePointAt(0)
            .toLong()) {


    override fun bind(viewBinding: ResultKanjiItemBinding, position: Int) {
        viewBinding.kanjiText.text = kanji
        viewBinding.root.setOnClickListener {
            onKanjiSelected(kanji)
        }
    }

    override fun getLayout() = R.layout.result_kanji_item

    override fun equals(other: Any?): Boolean {
        if (other !is ResultKanjiItem)
            return false

        return other.kanji == kanji
    }

    override fun initializeViewBinding(view: View) =
        ResultKanjiItemBinding.bind(view)

    override fun hashCode(): Int {
        return kanji.hashCode()
    }
}