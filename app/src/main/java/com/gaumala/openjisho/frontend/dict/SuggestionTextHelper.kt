package com.gaumala.openjisho.frontend.dict

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import com.gaumala.openjisho.R

/**
 * This class creates the char sequence for suggestions.
 * It adds the necessary spannables for bold text and clickable words.
 *
 * There is some hardcoded text for simplicity because we have no plans for
 * translating this app to other languages.
 */
object SuggestionTextHelper {
    fun getSuggestionsCharSequence(
        ctx: Context,
        initialText: String,
        suggestedQueries: List<String>,
        onSuggestionClicked: (String) -> Unit
    ): CharSequence {
        val boldSpan = StyleSpan(Typeface.BOLD)
        val suggestionSpan1 =
            if (suggestedQueries.size < 1) null else object : ClickableSpan() {
                override fun onClick(p0: View) {
                    val suggestion = suggestedQueries[0]
                    onSuggestionClicked(suggestion)
                }
            }
        val suggestionSpan2 =
            if (suggestedQueries.size < 2) null else object : ClickableSpan() {
                override fun onClick(p0: View) {
                    val suggestion = suggestedQueries[1]
                    onSuggestionClicked(suggestion)
                }
            }

        return SpannableStringBuilder().apply {
            if (initialText.isNotEmpty()) append(initialText)

            if (suggestionSpan1 != null) {
                val boldStart = length
                append(ctx.getString(R.string.tip) + ' ')
                val boldEnd = length
                setSpan(
                    boldSpan,
                    boldStart,
                    boldEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                append(ctx.getString(R.string.tip_start) + ' ')

                val suggestionStart = length
                append(suggestedQueries[0])
                val suggestionEnd = length
                setSpan(
                    suggestionSpan1,
                    suggestionStart,
                    suggestionEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            }

            if (suggestionSpan2 != null) {
                append(" or ")
                val suggestionStart = length
                append(suggestedQueries[1])
                val suggestionEnd = length
                setSpan(
                    suggestionSpan2,
                    suggestionStart,
                    suggestionEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            if (suggestionSpan1 != null) {
                append(' ')
                append(ctx.getString(R.string.tip_end))
            }
        }
    }
}