package com.gaumala.openjisho.frontend.dict.recycler

import android.text.method.LinkMovementMethod
import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.DictErrorItemBinding
import com.gaumala.openjisho.frontend.dict.SuggestionTextHelper.getSuggestionsCharSequence
import com.gaumala.openjisho.utils.image.MatrixCalculator
import com.xwray.groupie.viewbinding.BindableItem

class ErrorWithSuggestionsItem (
    private val originalQuery: String,
    private val suggestedQueries: List<String>,
    private val onSuggestionClicked: (String) -> Unit
): BindableItem<DictErrorItemBinding>(0) {

    override fun bind(viewBinding: DictErrorItemBinding, position: Int) {
        val context = viewBinding.root.context
        viewBinding.errorText.text =
            context.getString(R.string.not_found, originalQuery)
        viewBinding.tipText.apply {
            text = getSuggestionsCharSequence(
                context,
                "",
                suggestedQueries,
                onSuggestionClicked
            )
            movementMethod = LinkMovementMethod.getInstance()
            visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun getLayout() = R.layout.dict_error_item

    override fun equals(other: Any?): Boolean {
        val otherItem = other as? ErrorWithSuggestionsItem

        return suggestedQueries == otherItem?.suggestedQueries
                && originalQuery == otherItem.originalQuery
    }

    override fun hashCode(): Int {
        return suggestedQueries.hashCode()
    }

    override fun initializeViewBinding(view: View): DictErrorItemBinding {
        val binding = DictErrorItemBinding.bind(view)
        binding.notFoundArt.matrixCalculator = MatrixCalculator.FitTop()
        return binding
    }
}