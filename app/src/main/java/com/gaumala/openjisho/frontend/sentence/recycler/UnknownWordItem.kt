package com.gaumala.openjisho.frontend.sentence.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.UnknownWordItemBinding
import com.gaumala.openjisho.utils.makeTextSelectable
import com.xwray.groupie.viewbinding.BindableItem

class UnknownWordItem(
    val word: String,
    val position: Int
): BindableItem<UnknownWordItemBinding>(position.toLong()) {
    override fun bind(viewBinding: UnknownWordItemBinding, position: Int) {
        with(viewBinding.textView) {
            text = word
            makeTextSelectable()
        }
    }

    override fun getLayout() = R.layout.unknown_word_item

    override fun equals(other: Any?): Boolean {
        if (other !is UnknownWordItem)
            return false

        return other.position == position
    }

    override fun hashCode(): Int {
        return position
    }

    override fun initializeViewBinding(view: View) =
        UnknownWordItemBinding.bind(view)
}