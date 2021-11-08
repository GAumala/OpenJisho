package com.gaumala.openjisho.backend.setup.kanjidic

import android.util.Xml
import com.gaumala.openjisho.common.KanjidicEntry
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.File
import java.util.*

/**
 * Parser for the KANJIDIC XML file
 */
object KanjidicParser {

    private data class EntryBuilder(
        var literal: String?,
        var strokeCount: Int?,
        var grade: Int?,
        var jlpt: Int?,
        val meanings: MutableList<String>,
        val onReadings: MutableList<String>,
        val kunReadings: MutableList<String>
    ) {

        fun build(): KanjidicEntry {
            return KanjidicEntry(
                literal = literal!!,
                strokeCount = strokeCount!!,
                grade = grade ?: 0,
                jlpt = jlpt ?: 0,
                meanings = meanings,
                onReadings = onReadings,
                kunReadings = kunReadings
            )
        }
    }

    private fun createEntryBuilder() = EntryBuilder(
        null,
        null,
        null,
        null,
        LinkedList(), LinkedList(), LinkedList()
    )

    private fun parseLiteral(xpp: XmlPullParser): String {
        xpp.next()
        val literal = xpp.text
        xpp.next() // end tag
        return literal
    }

    private fun parseInt(xpp: XmlPullParser): Int {
        xpp.next()
        val literal = xpp.text.toInt()
        xpp.next() // end tag
        return literal
    }

    private fun parseReading(xpp: XmlPullParser, builder: EntryBuilder) {
        val type = xpp.getAttributeValue(null, "r_type")
        xpp.next()
        when (type) {
            "ja_on" -> builder.onReadings.add(xpp.text)
            "ja_kun" -> builder.kunReadings.add(xpp.text)
        }
        xpp.next() // end tag
    }

    private fun parseMeaning(xpp: XmlPullParser, builder: EntryBuilder) {
        val type = xpp.getAttributeValue(null, "m_lang")
        xpp.next()
        if (type == null) builder.meanings.add(xpp.text)
        xpp.next() // end tag
    }
    private fun parseEntry(xpp: XmlPullParser, builder: EntryBuilder): KanjidicEntry {
        while (!(xpp.eventType == XmlPullParser.END_TAG && xpp.name == "character")) {
            xpp.next()

            if (xpp.eventType == XmlPullParser.START_TAG) {
                when (xpp.name) {
                    "literal" -> {
                        val newLiteral = parseLiteral(xpp)
                        builder.literal = newLiteral
                    }

                    "grade" -> {
                        val newGrade = parseInt(xpp)
                        builder.grade = newGrade
                    }

                    "stroke_count" -> {
                        val newStrokeCount = parseInt(xpp)
                        builder.strokeCount = newStrokeCount
                    }

                    "jlpt" -> {
                        val newJlpt = parseInt(xpp)
                        builder.jlpt = newJlpt
                    }

                    "reading" -> {
                        parseReading(xpp, builder)
                    }

                    "meaning" -> {
                        parseMeaning(xpp, builder)
                    }
                }
            }

        }

        xpp.next()
        return builder.build()
    }

    private fun skipToKanjidicTag(xpp: XmlPullParser) {
        while (!(xpp.eventType == XmlPullParser.START_TAG
                    && xpp.name == "kanjidic2")
        )
            try {
                xpp.next()

                if (xpp.eventType == XmlPullParser.END_DOCUMENT)
                    throw IllegalStateException("Unexpected end of document")
            } catch (e: XmlPullParserException) {
                /* Apparently XmlPullParser cant handle the ELEMENT
                   definitions at the beginning of JMdict. Let's
                   ignore the execeptions and advance to the
                   <JMdict> tag.
                 */
            }
    }

    private fun parseTopLevelTag(xpp: XmlPullParser, callback: (KanjidicEntry, Int) -> Unit) {
        when {
            xpp.eventType == XmlPullParser.START_TAG && xpp.name == "character" -> {
                val newEntry = parseEntry(
                    xpp,
                    createEntryBuilder()
                )
                callback(newEntry, xpp.lineNumber)
             }
            else -> xpp.next()
        }
    }

    /**
     * parses the provided [[dictFile]] and executes [[callback]] for
     * every parsed entry. Blocks the calling thread until the whole
     * file is parsed.
     */
    fun exec(dictFile: File, callback: (KanjidicEntry, Int) -> Unit) {

        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setFeature(Xml.FEATURE_RELAXED, true)
        xpp.setInput(BufferedReader(dictFile.reader()))


        while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
            when (xpp.eventType) {
                XmlPullParser.START_DOCUMENT -> skipToKanjidicTag(
                    xpp
                )
                XmlPullParser.START_TAG -> parseTopLevelTag(
                    xpp,
                    callback
                )
                else -> xpp.next()
            }
        }
    }
}