package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.PickerWelcomeItemBinding
import com.gaumala.openjisho.utils.image.MatrixCalculator
import com.xwray.groupie.viewbinding.BindableItem

class PickerWelcomeItem(private val isSentence: Boolean)
    : BindableItem<PickerWelcomeItemBinding>(0) {

    override fun bind(viewBinding: PickerWelcomeItemBinding,
                      position: Int) {
        val textResId =
            if (isSentence) R.string.picker_welcome_sentences
            else R.string.picker_welcome_entries
        viewBinding.welcomeText.setText(textResId)
    }

    override fun getLayout() = R.layout.picker_welcome_item

    override fun equals(other: Any?): Boolean {
        return other is PickerWelcomeItem
    }

    override fun hashCode(): Int {
        return 1
    }

    override fun initializeViewBinding(view: View): PickerWelcomeItemBinding {
        val binding = PickerWelcomeItemBinding.bind(view)
        binding.welcomeArt.matrixCalculator = MatrixCalculator.FitTop()
        return binding
    }
}