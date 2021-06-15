package com.gaumala.openjisho.backend.setup.kanjidic

import com.gaumala.openjisho.backend.db.KanjidicRow
import com.gaumala.openjisho.backend.toJSONArray
import com.gaumala.openjisho.backend.toStringList
import com.gaumala.openjisho.common.KanjidicEntry
import org.json.JSONObject

object KanjidicConverter {

    private fun columnsToJSON(entry: KanjidicEntry): JSONObject {
        val result = JSONObject()
        result.put("grade", entry.grade)
        result.put("jlpt", entry.jlpt)
        result.put("meanings", entry.meanings.toJSONArray())
        result.put("onReadings", entry.onReadings.toJSONArray())
        result.put("kunReadings", entry.kunReadings.toJSONArray())
        return result
    }

    fun fromKanjiRow(kanjidicRow: KanjidicRow): KanjidicEntry {
        val extraColumns = JSONObject(kanjidicRow.entryJson)
        return KanjidicEntry(
            literal = kanjidicRow.literal,
            grade = extraColumns.getInt("grade"),
            strokeCount = kanjidicRow.strokes,
            jlpt = extraColumns.getInt("jlpt"),
            meanings = extraColumns.getJSONArray("meanings").toStringList(),
            onReadings = extraColumns.getJSONArray("onReadings").toStringList(),
            kunReadings = extraColumns.getJSONArray("kunReadings").toStringList()
        )
    }

    fun toDBRow(entry: KanjidicEntry): KanjidicRow {
        return KanjidicRow(
            literal = entry.literal,
            strokes = entry.strokeCount,
            entryJson = columnsToJSON(entry).toString())
    }
}