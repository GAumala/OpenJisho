package com.gaumala.openjisho.frontend.sentence.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.databinding.SentenceTranslationItemBinding
import com.gaumala.openjisho.utils.makeTextSelectable
import com.xwray.groupie.viewbinding.BindableItem

class TranslationItem(
    val sentence: Sentence
): BindableItem<SentenceTranslationItemBinding>(sentence.id) {
    override fun bind(viewBinding: SentenceTranslationItemBinding, position: Int) {
        with(viewBinding.textView) {
            text = sentence.english
            makeTextSelectable()
        }
    }

    override fun getLayout() = R.layout.sentence_translation_item

    override fun equals(other: Any?): Boolean {
        if (other !is TranslationItem)
            return false

        return other.sentence == sentence
    }

    override fun hashCode(): Int {
        return sentence.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        SentenceTranslationItemBinding.bind(view)
}