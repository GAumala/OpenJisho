package com.gaumala.openjisho.backend.lists

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.utils.data.DataPrinter
import com.gaumala.openjisho.utils.data.MalformedDataException
import com.gaumala.openjisho.backend.mapIndexed
import com.gaumala.openjisho.backend.mapJSON
import com.gaumala.openjisho.backend.setup.jmdict.JMdictConverter
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicConverter
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.common.StudyCard
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class StudyListJSONPrinter(private val db: DB): DataPrinter<List<StudyCard>> {

    companion object {
        private const val jmdictKey = "jmdictKey"
        private const val kanjidicKey = "kanjidicKey"
        private const val textKey = "textKey"
        private const val japaneseKey = "japaneseKey"
        private const val englishKey = "englishKey"
        private const val hintKey = "hintKey"
        private const val sourceKey = "sourceKey"
    }

    private fun getEntryHint(entry: JMdictEntry): String {
        val kanjiElements = entry.kanjiElements
        val readingElements = entry.readingElements
        return when {
            kanjiElements.isNotEmpty() -> kanjiElements.first().text
            readingElements.isNotEmpty() -> readingElements.first().text
            else -> "N/A"
        }
    }

    private fun getEntryHint(entry: KanjidicEntry): String {
        return entry.literal
    }

    private fun toJSON(item: StudyCard): JSONObject =
        when (item) {
            is StudyCard.JMdict -> {
                val result = JSONObject()
                result.put(jmdictKey, item.summarized.entry.entryId)
                result.put(hintKey, getEntryHint(item.summarized.entry))
                result
            }

            is StudyCard.Kanjidic -> {
                val result = JSONObject()
                result.put(kanjidicKey, item.entry.literal)
                result.put(hintKey, getEntryHint(item.entry))
                result
            }

            is StudyCard.Sentence -> {
                val result = JSONObject()
                result.put(japaneseKey, item.japanese)
                result.put(englishKey, item.english)
                result
            }

            is StudyCard.Text -> {
                val result = JSONObject()
                result.put(textKey, item.text)
                result
            }

            is StudyCard.NotFound -> {
                val result = JSONObject()
                result.put(sourceKey, item.source)
                result.put(hintKey, item.hint)
                result
            }
        }

    private fun toJSONArray(items: List<StudyCard>): JSONArray {
        return items.mapJSON { toJSON(it) }
    }

    private fun fromJSON(id: Long, json: JSONObject): StudyCard {
        val jmdictEntryId = json.optLong(jmdictKey, -1)
        if (jmdictEntryId > -1) {
            val hint = json.optString(hintKey)
            val entry = db.getJMDictEntry(jmdictEntryId)
            return if (entry == null)
                StudyCard.NotFound(id = id, source = "JMdict", hint = hint)
            else
                StudyCard.JMdict(id, entry)
        }

        val kanjidicEntryId = json.optString(kanjidicKey)
        if (kanjidicEntryId.isNotEmpty()) {
            val hint = json.optString(hintKey)
            val entry = db.getKanjidicEntry(kanjidicEntryId)
            return if (entry == null)
                StudyCard.NotFound(id = id, source = "Kanjidic", hint = hint)
            else
                StudyCard.Kanjidic(id, entry)
        }

        val japaneseText = json.optString(japaneseKey)
        if (japaneseText.isNotEmpty()) {
            val englishText = json.optString(
                englishKey, "Translation not found")
            return StudyCard.Sentence(id = id,
                japanese = japaneseText,
                english = englishText)
        }

        val text = json.optString(textKey)
        if (text.isNotEmpty()) {
            return StudyCard.Text(id, text)
        }

        val source = json.optString(sourceKey, "???")
        val hint = json.optString(hintKey)

        return StudyCard.NotFound(id, source, hint)
    }

    private fun fromJSONArray(jsonArray: JSONArray): List<StudyCard> {
        return jsonArray.mapIndexed { index, obj ->
            fromJSON(index.toLong(), obj)
        }
    }

    override fun print(value: List<StudyCard>): String {
        return toJSONArray(value).toString()
    }

    override fun scan(printed: String): List<StudyCard> {
        try {
            return fromJSONArray(JSONArray(printed))
        } catch (ex: JSONException) {
            throw MalformedDataException(ex)
        }
    }

    override val ext = "json"

    interface DB {
        fun getJMDictEntry(id: Long): JMdictEntry?
        fun getKanjidicEntry(literal: String): KanjidicEntry?
    }

    class RoomDB(private val dao: DictQueryDao): DB {
        override fun getJMDictEntry(id: Long): JMdictEntry? {
            val row = dao.lookupJMdictRowById(id) ?: return null
            return JMdictConverter.fromEntryRow(row)
        }

        override fun getKanjidicEntry(literal: String): KanjidicEntry? {
            val row = dao.lookupKanjidicRowExact(literal) ?: return null
            return KanjidicConverter.fromKanjiRow(row)
        }
    }
}