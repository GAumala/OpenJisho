package com.gaumala.openjisho.backend.setup.jmdict

import android.util.Xml
import com.gaumala.openjisho.common.JMdictEntry
import java.io.File
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.lang.IllegalStateException
import java.util.*


/**
 * Parser for the JMdict XML file
 */
object JMdictParser {

    private val entityDelimetersRegex = Regex("&|;")

    private data class EntryBuilder(
        var id: Long?,
        val kanjiElements: MutableList<JMdictEntry.Element>,
        val readingElements: MutableList<JMdictEntry.Element>,
        val senseElements: MutableList<JMdictEntry.Sense>
    ) {

        fun build(): JMdictEntry {
            return JMdictEntry(
                entryId = id!!,
                kanjiElements = kanjiElements,
                readingElements = readingElements,
                senseElements = senseElements
            )
        }
    }

    private fun createEntryBuilder() = EntryBuilder(
        null,
        LinkedList(), LinkedList(), LinkedList()
    )

    private fun parse_ent_seq(xpp: XmlPullParser): Long {
        xpp.next()
        val id = xpp.text.toLong()
        xpp.next() // end tag
        xpp.next() // next tag
        return id
    }

    private fun parse_k_ele(xpp: XmlPullParser): JMdictEntry.Element {
        var result: String? = null
        val tags = LinkedList<JMdictEntry.Tag>()

        while (!(xpp.eventType == XmlPullParser.END_TAG && xpp.name == "k_ele")) {
            xpp.next()

            if (xpp.eventType == XmlPullParser.START_TAG) {
                when (xpp.name) {
                    "keb" -> {
                        xpp.next()
                        result = xpp.text
                    }
                    "ke_pri" -> {
                        xpp.next()
                        // We are only interested in the frequency indicator
                        if (xpp.text.startsWith("nf"))
                            tags.add(JMdictEntry.Tag.parse(xpp.text))
                    }
                }
            }
        }

        if (result == null)
            throw IllegalStateException("Unable to parse 'k_ele' tag at ${xpp.lineNumber}")

        xpp.next()
        return JMdictEntry.Element(result, tags)
    }

    private fun parse_r_ele(xpp: XmlPullParser): JMdictEntry.Element {
        var result: String? = null
        val tags = LinkedList<JMdictEntry.Tag>()

        while (!(xpp.eventType == XmlPullParser.END_TAG && xpp.name == "r_ele")) {
            xpp.next()

            if (xpp.eventType == XmlPullParser.START_TAG) {
                when (xpp.name) {
                    "reb" -> {
                        xpp.next()
                        result = xpp.text
                    }
                    "re_pri" -> {
                        xpp.next()
                        // We are only interested in the frequency indicator
                        if (xpp.text.startsWith("nf"))
                            tags.add(JMdictEntry.Tag.parse(xpp.text))
                    }
                }
            }
        }

        if (result == null)
            throw IllegalStateException("Unable to parse 'k_ele' tag at ${xpp.lineNumber}")

        xpp.next()
        return JMdictEntry.Element(result, tags)
    }

    private fun parse_sense(xpp: XmlPullParser): JMdictEntry.Sense {
        val result = LinkedList<String>()
        val tags = LinkedList<JMdictEntry.Tag>()

        while (!(xpp.eventType == XmlPullParser.END_TAG && xpp.name == "sense")) {
            xpp.next()

            if (xpp.eventType == XmlPullParser.START_TAG) {
                when (xpp.name) {
                    "pos" -> {
                        xpp.next()
                        val code = xpp.text.replace(entityDelimetersRegex, "")
                        tags.add(JMdictEntry.Tag.parse(code))
                    }
                    "dial" -> {
                        xpp.next()
                        val code = xpp.text.replace(entityDelimetersRegex, "")
                        tags.add(JMdictEntry.Tag.parse(code))
                    }
                    "field" -> {
                        xpp.next()
                        val code = xpp.text.replace(entityDelimetersRegex, "")
                        tags.add(JMdictEntry.Tag.parse(code))
                    }
                    "misc" -> {
                        xpp.next()
                        val code = xpp.text.replace(entityDelimetersRegex, "")
                        tags.add(JMdictEntry.Tag.parse(code))
                    }
                    "gloss" -> {
                        xpp.next()
                        result.add(xpp.text)
                    }
                }
            }
        }

        if (result.isEmpty())
            throw IllegalStateException("Unable to parse 'sense' tag at ${xpp.lineNumber}")

        xpp.next()
        return JMdictEntry.Sense(glossItems = result, glossTags = tags)
    }

    private fun parseEntry(xpp: XmlPullParser, builder: EntryBuilder): JMdictEntry {
        while (!(xpp.eventType == XmlPullParser.END_TAG && xpp.name == "entry")) {
            xpp.next()

            if (xpp.eventType == XmlPullParser.START_TAG) {
                when (xpp.name) {
                    "ent_seq" -> {
                        val newId = parse_ent_seq(xpp)
                        builder.id = newId
                    }

                    "k_ele" -> {
                        val newKanji = parse_k_ele(xpp)
                        builder.kanjiElements.add(newKanji)
                    }

                    "r_ele" -> {
                        val newReading = parse_r_ele(xpp)
                        builder.readingElements.add(newReading)
                    }

                    "sense" -> {
                        val newSense = parse_sense(xpp)
                        builder.senseElements.add(newSense)
                    }
                }
            }

        }

        xpp.next()
        return builder.build()
    }

    private fun skipToJMdictTag(xpp: XmlPullParser) {
        while (!(xpp.eventType == XmlPullParser.START_TAG
                    && xpp.name == "JMdict")
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

    private fun parseTopLevelTag(xpp: XmlPullParser, callback: (JMdictEntry, Int) -> Unit) {
        when {
            xpp.name == "JMdict" -> xpp.next()
            xpp.name == "entry" -> {
                val newEntry = parseEntry(
                    xpp,
                    createEntryBuilder()
                )
                callback(newEntry, xpp.lineNumber)
            }
            else -> {
                val msg = "Unknown tag '${xpp.name}', expected 'entry' at line ${xpp.lineNumber}"
                throw IllegalStateException(msg)
            }
        }
    }

    /**
     * parses the provided [[dictFile]] and executes [[callback]] for
     * every parsed entry. Blocks the calling thread until the whole
     * file is parsed.
     */
    fun exec(dictFile: File, callback: (JMdictEntry, Int) -> Unit) {

        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setFeature(Xml.FEATURE_RELAXED, true)
        xpp.setInput(BufferedReader(dictFile.reader()))


        while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
            when (xpp.eventType) {
                XmlPullParser.START_DOCUMENT -> skipToJMdictTag(xpp)
                XmlPullParser.START_TAG -> parseTopLevelTag(
                    xpp,
                    callback
                )
                else -> xpp.next()
            }
        }
    }
}