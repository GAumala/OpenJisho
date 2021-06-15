package com.gaumala.openjisho.frontend.entry

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.databinding.EntrySectionCardBinding
import com.gaumala.openjisho.utils.getResIdFromTheme
import com.gaumala.openjisho.utils.setTextAppearanceCompat
import com.google.android.material.chip.ChipGroup

object EntrySectionsViewFactory {

    private fun addLineDivider(layout: LinearLayout) {
        val ctx = layout.context
        val view = View(ctx)
        val drawable = ColorDrawable(
            ContextCompat.getColor(ctx, R.color.divider_light_gray))
        view.background = drawable

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ctx.resources.getDimension(R.dimen.light_divider_height).toInt())
        val verticalMargin = ctx.resources.getDimension(R.dimen.text_item_top_margin)
        layoutParams.setMargins(0, verticalMargin.toInt(), 0, verticalMargin.toInt())

        layout.addView(view, layoutParams)
    }

    private fun addTextView(layout: ViewGroup, text: String, isLarge: Boolean) {
        val ctx = layout.context
        val textView = TextView(ctx)
        val style = if (isLarge) R.style.TextAppearance_AppCompat_Large
        else R.style.TextAppearance_AppCompat_Medium
        textView.setTextAppearanceCompat(ctx, style)
        textView.text = text
        textView.setTextIsSelectable(true)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        layout.addView(textView, layoutParams)
    }

    private fun addKanji(layout: LinearLayout, kanjidicEntry: KanjidicEntry) {
        val ctx = layout.context
        val view = LayoutInflater.from(ctx)
            .inflate(R.layout.kanjidic_section, layout, false)
        val meaningTextView = view.findViewById<TextView>(R.id.meaning_text)
        val subtitleTextView = view.findViewById<TextView>(R.id.subtitle_text)
        val kanjiTextView = view.findViewById<TextView>(R.id.kanji_text)
        val onTextView = view.findViewById<TextView>(R.id.on_text)
        val kunTextView = view.findViewById<TextView>(R.id.kun_text)

        kanjiTextView.text = kanjidicEntry.literal
        subtitleTextView.text = getKanjiSubtitle(kanjidicEntry)
        meaningTextView.text = kanjidicEntry.meanings.joinToString(separator = ", ")
        onTextView.text = kanjidicEntry.onReadings.joinToString(separator = ", ")
        kunTextView.text = kanjidicEntry.kunReadings.joinToString(separator = ", ")

        kanjiTextView.setTextIsSelectable(true)
        subtitleTextView.setTextIsSelectable(true)
        meaningTextView.setTextIsSelectable(true)
        onTextView.setTextIsSelectable(true)
        kunTextView.setTextIsSelectable(true)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        layout.addView(view, layoutParams)
    }

    private fun getKanjiSubtitle(kanjidicEntry: KanjidicEntry): String {
        val strokes = if(kanjidicEntry.strokeCount == 1) "1 stroke"
        else "${kanjidicEntry.strokeCount} strokes"
        if (kanjidicEntry.jlpt == 0)
            return strokes

        return "$strokes, JLPT N${kanjidicEntry.jlpt}"

    }

    private fun createTag(ctx: Context): TextView {
        val tagView = TextView(ctx)
        tagView.textSize = 15f
        tagView.setTextColor(ContextCompat.getColor(ctx, R.color.tag_color))
        tagView.setBackgroundResource(R.drawable.tag_bg)
        val vPadding = ctx.resources.getDimension(R.dimen.tag_v_padding).toInt()
        val hPadding = ctx.resources.getDimension(R.dimen.tag_h_padding).toInt()
        tagView.setPadding(hPadding, vPadding, hPadding, vPadding)

        tagView.layoutParams = ChipGroup.LayoutParams(
            ChipGroup.LayoutParams.WRAP_CONTENT,
            ChipGroup.LayoutParams.WRAP_CONTENT)

        return tagView

    }

    private fun addFrequencyTag(layout:ViewGroup, tag: JMdictEntry.Tag)  {
        val ctx = layout.context

        val tagView = TextView(ctx)
        tagView.setTextAppearanceCompat(
            ctx,
            ctx.getResIdFromTheme(R.attr.textAppearanceOverline))
        tagView.text = tag.getText(ctx)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        layout.addView(tagView, layoutParams)
    }

    private fun addChipGroup(layout: ViewGroup, tags: List<JMdictEntry.Tag>) {
        val ctx = layout.context
        val chipGroup = ChipGroup(ctx)
        chipGroup.chipSpacingVertical = chipGroup.chipSpacingHorizontal
        tags.forEach {
            val newTag = createTag(ctx)
            newTag.text = it.getText(newTag.context)
            chipGroup.addView(newTag)
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        val bottomMargin = ctx.resources.getDimension(R.dimen.chip_group_bottom_margin)
        layoutParams.setMargins(0, 0, 0, bottomMargin.toInt())

        layout.addView(chipGroup, layoutParams)
    }

    private fun buildDefinitionsString(defs: List<String>): String {
        val builder = defs.foldIndexed(StringBuilder()) { index, builder, def ->
            builder.append("${index + 1}. $def")
            if (index + 1 < defs.size)
                builder.append("\n")
            builder
        }
        return builder.toString()
    }

    fun EntrySectionCardBinding.bindFormsSection(elements: List<JMdictEntry.Element>) {
        val ctx = root.context
        headerText.visibility = View.VISIBLE
        headerText.setText(R.string.forms_header)
        headerText.setPadding(0, 0, 0, ctx.resources.getDimensionPixelSize(R.dimen.padding_16))

        val totalElements = elements.size
        elements.forEachIndexed { index, element ->
            val freqTag = element.tags.firstOrNull()
            if (freqTag != null)
                addFrequencyTag(linearLayout, freqTag)

            addTextView(linearLayout, element.text, isLarge = true)

            if (index + 1 < totalElements)
                addLineDivider(linearLayout)
        }
    }

    fun EntrySectionCardBinding.bindKanjiSection(
        entries: List<KanjidicEntry>,
        onlySection: Boolean) {

        if (onlySection) {
            headerText.visibility = View.GONE
        } else {
            headerText.visibility = View.VISIBLE
            headerText.setText(R.string.kanji_header)
        }

        val totalElements = entries.size
        entries.forEachIndexed { index, entry ->
            addKanji(linearLayout, entry)

            if (index + 1 < totalElements)
                addLineDivider(linearLayout)
        }
    }

    fun EntrySectionCardBinding.bindReadingSection(elements: List<JMdictEntry.Element>) {
        val ctx = root.context
        headerText.setText(R.string.reading_header)
        headerText.setPadding(0, 0, 0,
            ctx.resources.getDimensionPixelSize(R.dimen.padding_16))

        val totalElements = elements.size
        elements.forEachIndexed { index, element ->
            val freqTag = element.tags.firstOrNull()
            if (freqTag != null)
                addFrequencyTag(linearLayout, freqTag)

            addTextView(linearLayout, element.text, isLarge = true)

            if (index + 1 < totalElements)
                addLineDivider(linearLayout)
        }
    }

    fun EntrySectionCardBinding.bindSenseSection(elements: List<JMdictEntry.Sense>) {
        headerText.setText(R.string.sense_header)
        headerText.setPadding(0, 0, 0,
            root.context.resources.getDimensionPixelSize(R.dimen.padding_16))

        val totalElements = elements.size
        elements.forEachIndexed { index, element ->
            if (element.glossTags.isNotEmpty())
                addChipGroup(linearLayout, element.glossTags)

            val definitions = buildDefinitionsString(element.glossItems)
            addTextView(linearLayout, definitions, isLarge = false)

            if (index + 1 < totalElements)
                addLineDivider(linearLayout)
        }
    }

    fun createViews(parent: ViewGroup, sections: List<Section>): List<View> {
        val inflater = LayoutInflater.from(parent.context)
        val isOnlySection = sections.size == 1
        return sections.map {
            val binding =
                EntrySectionCardBinding.inflate(inflater, parent, false)
            when (it) {
                is Section.Form -> binding.bindFormsSection(it.kanjiElements)
                is Section.Reading -> binding.bindReadingSection(it.readingElements)
                is Section.Sense -> binding.bindSenseSection(it.senseElements)
                is Section.Kanji -> binding.bindKanjiSection(it.kanjiEntries, isOnlySection)
            }
            binding.root
        }
    }
}