package com.gaumala.openjisho.frontend.entry

import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
import java.util.*

sealed class Section {
    data class Form(val kanjiElements: List<JMdictEntry.Element>): Section()
    data class Kanji(val kanjiEntries: List<KanjidicEntry>): Section()
    data class Reading(val readingElements: List<JMdictEntry.Element>): Section()
    data class Sense(val senseElements: List<JMdictEntry.Sense>): Section()

    companion object {

        fun fromKanjidicEntry(entry: KanjidicEntry): List<Section> {
            val result = LinkedList<Section>()
            result.add(Kanji(listOf(entry)))
            return result
        }

        fun fromJMdictEntry(entry: JMdictEntry, headword: String): List<Section> {
            val result = LinkedList<Section>()
            val kanjiElements = entry.kanjiElements
            val readingElements = entry.readingElements
            val senseElements = entry.senseElements

            if (kanjiElements.isNotEmpty()) {
                val otherForms = kanjiElements.filter { it.text != headword }
                if (otherForms.isNotEmpty())
                    result.add(Form(otherForms))
            }

            if (readingElements.isNotEmpty()) {
                val otherReadings = readingElements.filter { it.text != headword }
                if (otherReadings.isNotEmpty())
                    result.add(Reading(otherReadings))
            }

            result.add(Sense(senseElements))
            return result
        }
    }
}