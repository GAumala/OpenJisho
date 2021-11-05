package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.databinding.DictErrorItemBinding
import com.gaumala.openjisho.utils.image.MatrixCalculator
import com.xwray.groupie.viewbinding.BindableItem

class ErrorItem(val message: UIText): BindableItem<DictErrorItemBinding>(0) {

    override fun bind(viewBinding: DictErrorItemBinding, position: Int) {
        val textView =  viewBinding.errorText
        textView.text = message.getText(textView.context)
        // ErrorWithSuggestionsItem and this class share view holders so it's
        // important to clear data before reusing bindings.
        viewBinding.tipText.visibility = View.GONE
    }

    override fun getLayout() = R.layout.dict_error_item

    override fun equals(other: Any?): Boolean {
        val otherItem = other as? ErrorItem

        return message == otherItem?.message
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }

    override fun initializeViewBinding(view: View): DictErrorItemBinding {
        val binding = DictErrorItemBinding.bind(view)
        binding.notFoundArt.matrixCalculator = MatrixCalculator.FitTop()
        return binding
    }
}
