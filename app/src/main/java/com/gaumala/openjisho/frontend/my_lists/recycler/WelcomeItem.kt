package com.gaumala.openjisho.frontend.my_lists.recycler

import android.view.View
import androidx.annotation.StringRes
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.WelcomeItemBinding
import com.gaumala.openjisho.utils.image.MatrixCalculator
import com.xwray.groupie.viewbinding.BindableItem

class WelcomeItem(@StringRes val textResId: Int)
    : BindableItem<WelcomeItemBinding>(0) {

    override fun getLayout() = R.layout.welcome_item

    override fun bind(viewBinding: WelcomeItemBinding, position: Int) {
        viewBinding.welcomeText.setText(textResId)
    }

    override fun equals(other: Any?): Boolean {
        return other is WelcomeItem
    }

    override fun hashCode(): Int {
        return 1
    }

    override fun initializeViewBinding(view: View): WelcomeItemBinding {
        val binding = WelcomeItemBinding.bind(view)
        binding.welcomeArt.matrixCalculator = MatrixCalculator.FitTop()
        return binding
    }
}