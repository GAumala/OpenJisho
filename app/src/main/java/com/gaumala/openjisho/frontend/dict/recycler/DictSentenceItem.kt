package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.databinding.DictSentenceItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class DictSentenceItem(val sentence: Sentence,
                       val onClicked: () -> Unit
): BindableItem<DictSentenceItemBinding>(sentence.id) {
    override fun bind(viewBinding: DictSentenceItemBinding, position: Int) {
        val binding = viewBinding.sentenceItem
        binding.headerText.text = sentence.japanese
        binding.subText.text = sentence.english

        viewBinding.card.setOnClickListener { onClicked() }
    }

    override fun getLayout() = R.layout.dict_sentence_item

    override fun equals(other: Any?): Boolean {
        if (other !is DictSentenceItem)
            return false

        return other.sentence == sentence
    }

    override fun hashCode(): Int {
        return sentence.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        DictSentenceItemBinding.bind(view)
}