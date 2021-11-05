package com.gaumala.openjisho.frontend.dict.recycler

import android.text.method.LinkMovementMethod
import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.SuggestionItemBinding
import com.gaumala.openjisho.frontend.dict.SuggestionTextHelper.getSuggestionsCharSequence
import com.xwray.groupie.viewbinding.BindableItem

class SuggestionItem(
    private val suggestedQueries: List<String>,
    private val onSuggestionClicked: (String) -> Unit
): BindableItem<SuggestionItemBinding>(0) {

    override fun bind(viewBinding: SuggestionItemBinding, position: Int) {
        viewBinding.textView.apply {
            text = getSuggestionsCharSequence(
                context,
                "",
                suggestedQueries,
                onSuggestionClicked
            )
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun getLayout() = R.layout.suggestion_item

    override fun equals(other: Any?): Boolean {
        return other is LoadingItem
    }

    override fun hashCode(): Int {
        return 1
    }

    override fun initializeViewBinding(view: View) =
        SuggestionItemBinding.bind(view)
}
