package com.gaumala.openjisho.backend

import com.gaumala.openjisho.common.JMdictEntry
import org.json.JSONArray
import org.json.JSONObject


fun List<String>.toJSONArray(): JSONArray {
    val array = JSONArray()
    this.forEach { array.put(it) }
    return array
}

fun <T> List<T>.mapJSON(fn: (T) -> JSONObject): JSONArray {
    val array = JSONArray()
    this.forEach { array.put(fn(it)) }
    return array
}

fun <T> JSONArray.map(fn: (JSONObject) -> T): List<T> {
    var i = 0
    val result = ArrayList<T>(this.length())
    while (i < this.length()) {
        result.add(fn(this.getJSONObject(i)))
        i++
    }
    return result
}

fun <T> JSONArray.mapIndexed(fn: (Int, JSONObject) -> T): List<T> {
    var i = 0
    val result = ArrayList<T>(this.length())
    while (i < this.length()) {
        result.add(fn(i, this.getJSONObject(i)))
        i++
    }
    return result
}

fun JSONArray.toStringList(): List<String> {
    var i = 0
    val result: ArrayList<String> = ArrayList(this.length())
    while (i < this.length()) {
        result.add(this.getString(i))
        i++
    }
    return result
}

val elementToJSON: (JMdictEntry.Element) -> JSONObject = {
        element ->
    val result = JSONObject()
    result.put("text", element.text)
    result.put("tags",
        element.tags.map { it.getRawTag() }
            .toJSONArray())

    result
}



val senseToJSON: (JMdictEntry.Sense) -> JSONObject = {
        sense ->
    val result = JSONObject()
    result.put("glossItems", sense.glossItems.toJSONArray())
    result.put("glossTags",
        sense.glossTags.map { it.getRawTag() }
            .toJSONArray())

    result
}