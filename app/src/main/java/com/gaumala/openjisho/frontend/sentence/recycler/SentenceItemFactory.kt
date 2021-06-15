package com.gaumala.openjisho.frontend.sentence.recycler

import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.frontend.sentence.SentenceWord
import com.xwray.groupie.viewbinding.BindableItem
import java.lang.IllegalArgumentException

class SentenceItemFactory(
    private val onJMdictEntryClicked: (JMdictEntry.Summarized) -> Unit
) {
    fun createItems(sentence: Sentence,
                    words: List<SentenceWord>): List<BindableItem<*>> {
        val result = ArrayList<BindableItem<*>>()
        result.add(TranslationItem(sentence))

        if (words.isEmpty())
            return result

        result.addAll(words.mapIndexed { i, word ->
            when (word) {
                is SentenceWord.JMdict ->
                    SentenceJMdictItem(word.entry, i, onJMdictEntryClicked)
                is SentenceWord.Unknown -> UnknownWordItem(word.text, i)
            }
        })
        return result
    }
}