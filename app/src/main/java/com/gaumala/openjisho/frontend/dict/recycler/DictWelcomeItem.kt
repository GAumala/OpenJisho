package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.DictWelcomeItemBinding
import com.gaumala.openjisho.utils.image.MatrixCalculator
import com.xwray.groupie.viewbinding.BindableItem

class DictWelcomeItem(private val isSentence: Boolean): BindableItem<DictWelcomeItemBinding>(0) {

    override fun bind(viewBinding: DictWelcomeItemBinding, position: Int) {
        val textResId =
            if (isSentence) R.string.dict_welcome_sentences
            else R.string.dict_welcome_entries
        viewBinding.welcomeText.setText(textResId)
    }

    override fun getLayout() = R.layout.dict_welcome_item

    override fun equals(other: Any?): Boolean {
        return other is DictWelcomeItem
    }

    override fun hashCode(): Int {
        return 1
    }

    override fun initializeViewBinding(view: View): DictWelcomeItemBinding {
        val binding = DictWelcomeItemBinding.bind(view)
        binding.welcomeArt.matrixCalculator = MatrixCalculator.FitTop()
        return binding
    }
}