package com.gaumala.openjisho.backend.setup.jmdict

import com.gaumala.openjisho.backend.*
import com.gaumala.openjisho.backend.db.EngKeywordRow
import com.gaumala.openjisho.backend.db.JMdictRow
import com.gaumala.openjisho.backend.db.JpnKeywordRow
import com.gaumala.openjisho.backend.db.TagRow
import com.gaumala.openjisho.common.JMdictEntry
import org.json.JSONArray
import org.json.JSONObject

object JMdictConverter {

    private val jsonToElement: (JSONObject) -> JMdictEntry.Element = { json ->
        val text = json.getString("text")
        val tags = json.getJSONArray("tags")
            .toStringList()
            .map { JMdictEntry.Tag.parse(it) }
        JMdictEntry.Element(text, tags)
    }

    private val jsonToSense: (JSONObject) -> JMdictEntry.Sense = { json ->
        val glossItems = json.getJSONArray("glossItems")
            .toStringList()
        val glossTags = json.getJSONArray("glossTags")
            .toStringList()
            .map { JMdictEntry.Tag.parse(it) }
        JMdictEntry.Sense(glossItems = glossItems, glossTags = glossTags)
    }

    private fun elementsToJSON(
        serializedKanji: JSONArray,
        serializedReading: JSONArray,
        serializedSense: JSONArray
    ): JSONObject {
        val result = JSONObject()
        result.put("kanji", serializedKanji)
        result.put("reading", serializedReading)
        result.put("sense", serializedSense)
        return result
    }



    private fun JMdictEntry.getAllEntryTags(): Set<String> {
        return (kanjiElements.flatMap { it.tags }
                + readingElements.flatMap { it.tags }
                + senseElements.flatMap { it.glossTags })
            .map { it.getRawTag() }
            .toSet()
    }

    private fun parseEntryJSON(entryId: Long, jsonObject: JSONObject): JMdictEntry {
        val serializedKanji = jsonObject.getJSONArray("kanji")
        val serializedReading = jsonObject.getJSONArray("reading")
        val serializedSense = jsonObject.getJSONArray("sense")

        val kanjiElements = serializedKanji.map(jsonToElement)
        val readingElements = serializedReading.map(jsonToElement)
        val senseElements = serializedSense.map(jsonToSense)

        return JMdictEntry(
            entryId = entryId,
            kanjiElements = kanjiElements,
            readingElements = readingElements,
            senseElements = senseElements
        )
    }


    fun toDBRows(entry: JMdictEntry)
            : JMdictRowsHolder {
        val serializedKanjiElements =
            entry.kanjiElements.mapJSON(elementToJSON)
        val serializedReadingElements =
            entry.readingElements.mapJSON(elementToJSON)
        val serializedSenseElements =
            entry.senseElements.mapJSON(senseToJSON)

        val entryId = entry.entryId
        val entryJSON = elementsToJSON(
            serializedKanji = serializedKanjiElements,
            serializedReading = serializedReadingElements,
            serializedSense = serializedSenseElements
        )

        val entryRow = JMdictRow(entryId, entryJSON.toString())

        val jpnKeywordRows = (entry.kanjiElements + entry.readingElements).map {
            JpnKeywordRow(0, it.text, entryRow.id)
        }

        val engDefinitions = entry.senseElements
            .flatMap { it.glossItems }
            .joinToString(" ")
        val engKeywordRow = EngKeywordRow(
            0, entryId = entryId, keywords = engDefinitions
        )

        val tagRows = entry.getAllEntryTags().map {
            TagRow(id = 0, entryId = entryId, tag = it)
        }

        return JMdictRowsHolder(entryRow, jpnKeywordRows, engKeywordRow, tagRows)
    }

    fun fromEntryRow(jmDictRow: JMdictRow): JMdictEntry {
        val entryId = jmDictRow.id
        val jsonObject = JSONObject(jmDictRow.entryJson)
        return parseEntryJSON(entryId, jsonObject)
    }
}