package com.gaumala.openjisho.frontend.sentence.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.databinding.JmdictItemBinding
import com.gaumala.openjisho.frontend.dict.recycler.JMdictItem
import com.xwray.groupie.viewbinding.BindableItem

class SentenceJMdictItem(
    val usedForm: String?,
    val summarized: JMdictEntry.Summarized,
    val position: Int,
    val onClicked: (JMdictEntry.Summarized) -> Unit
) : BindableItem<JmdictItemBinding>(position.toLong()) {

    override fun bind(viewBinding: JmdictItemBinding, position: Int) {
        // For sentence word lists, we should display the form that's
        // used in sentence as the entry header instead of the header
        // in dictionary entry. This also means we have to remove the
        // furigana string because it may no longer match the header
        val displayHeader = usedForm ?: summarized.header
        val displayFurigana =
            if (usedForm != null && usedForm != summarized.header) null
            else summarized.furigana
        val displaySummary =
            summarized.copy(header = displayHeader, furigana = displayFurigana)
        JMdictItem(displaySummary).bind(viewBinding, position)

        // but if the user clicks on this item, the JMdict entry screen
        // should show the real header
        viewBinding.root.setOnClickListener {
            onClicked(summarized)
        }
    }

    override fun getLayout() = R.layout.jmdict_item

    override fun equals(other: Any?): Boolean {
        if (other !is SentenceJMdictItem)
            return false

        return other.summarized.entry.entryId == summarized.entry.entryId
                && other.usedForm == usedForm
    }

    override fun hashCode(): Int {
        return position
    }

    override fun initializeViewBinding(view: View) =
        JmdictItemBinding.bind(view)
}