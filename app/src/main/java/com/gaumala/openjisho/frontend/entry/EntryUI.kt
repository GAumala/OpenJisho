package com.gaumala.openjisho.frontend.entry

import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.gaumala.openjisho.R
import com.gaumala.mvi.BaseUI
import com.gaumala.openjisho.frontend.entry.EntrySectionsViewFactory.createViews
import com.gaumala.openjisho.utils.image.MatrixCalculator
import com.gaumala.openjisho.utils.image.MatrixImageView

class EntryUI(owner: LifecycleOwner,
              view: View,
              liveState: LiveData<EntryState>): BaseUI<EntryState>(owner, liveState) {

    private val layout = view.findViewById<LinearLayout>(R.id.linear_layout)

    private var lastRenderedSections: List<Section>? = null

    private fun addInvisibleDivider(layout: LinearLayout) {
        val ctx = layout.context
        val view = View(ctx)
        val drawable = ContextCompat.getDrawable(
            ctx, R.drawable.invisible_divider)
        view.background = drawable

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        layout.addView(view, layoutParams)
    }

    private fun populateWithSections(sections: List<Section>) {
        if (lastRenderedSections === sections || sections.isEmpty())
            return

        layout.removeAllViews()
        createViews(layout, sections).forEach {
            layout.addView(it)
            addInvisibleDivider(layout)
        }

        lastRenderedSections = sections
    }

    override fun rebind(state: EntryState) {
        populateWithSections(state.sections)
    }
}