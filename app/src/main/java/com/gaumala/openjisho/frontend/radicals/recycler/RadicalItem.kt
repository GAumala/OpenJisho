package com.gaumala.openjisho.frontend.radicals.recycler

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.RadicalItemBinding
import com.gaumala.openjisho.frontend.radicals.RadicalButtonState
import com.gaumala.openjisho.frontend.radicals.RadicalIndex
import com.xwray.groupie.viewbinding.BindableItem

class RadicalItem(private val radicalIndex: RadicalIndex,
                  private val onRadicalClicked: (RadicalIndex) -> Unit)
    : BindableItem<RadicalItemBinding>(
        radicalIndex.unicodeChar
            .codePointAt(0)
            .toLong()) {


    override fun bind(viewBinding: RadicalItemBinding, position: Int) {
        val ctx = viewBinding.root.context
        val buttonState = radicalIndex.buttonState

        viewBinding.radicalText.text = radicalIndex.unicodeChar
        when (buttonState) {
            RadicalButtonState.enabled -> {
                val textColor = ContextCompat.getColor(ctx, R.color.main_blue)
                viewBinding.radicalText.setTextColor(textColor)
                viewBinding.card.setCardBackgroundColor(Color.WHITE)
            }
            RadicalButtonState.selected -> {
                val backgroundColor = ContextCompat.getColor(ctx, R.color.main_blue)
                viewBinding.card.setCardBackgroundColor(backgroundColor)
                viewBinding.radicalText.setTextColor(Color.WHITE)
            }
            RadicalButtonState.disabled -> {
                val textColor = ContextCompat.getColor(ctx, R.color.dim_gray)
                viewBinding.radicalText.setTextColor(textColor)
                viewBinding.card.setCardBackgroundColor(Color.WHITE)
            }
        }

        when (buttonState) {
            RadicalButtonState.disabled ->
                viewBinding.root.setOnClickListener(null)

            else ->
                viewBinding.root.setOnClickListener {
                    onRadicalClicked(radicalIndex)
                }
        }
    }

    override fun getLayout() = R.layout.radical_item

    override fun equals(other: Any?): Boolean {
        if (other !is RadicalItem)
            return false

        return other.radicalIndex == radicalIndex
    }

    override fun hashCode(): Int {
        return radicalIndex.hashCode()
    }

    override fun initializeViewBinding(view: View) =
        RadicalItemBinding.bind(view)
}