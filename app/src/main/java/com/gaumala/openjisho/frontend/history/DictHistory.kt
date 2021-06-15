package com.gaumala.openjisho.frontend.history

import com.gaumala.openjisho.utils.AsyncFileHandler
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class DictHistory(private val fileHandler: AsyncFileHandler) {
    private lateinit var entries: LinkedList<String>

    private val parseEntries: (String) -> LinkedList<String> = {
        val list = LinkedList<String>()

        val array = JSONArray(if (it.isEmpty()) "[]" else it)
        val len = array.length()
        var i = 0

        try {
            while (i < len) {
                list.add(array.getString(i))
                i++
            }
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        list
    }

    private fun copyEntriesConcurrent(): List<String> {
        do {
            val failed: Boolean = try {
                return entries.toList()
            } catch (ex: NoSuchElementException) {
                // this method is called in a background thread so
                // it's possible that the main thread is removing elements
                // from the list while we are consuming the data here.
                // I should probably use a better data structure for concurrency
                // but for now let's just retry until it works.
                true
            }
        } while (failed)

        throw UnknownError("Broke out infinite loop")
    }

    private val serializeEntries: () -> String = {
        val array = JSONArray()
        val entriesCopy = copyEntriesConcurrent()

        entriesCopy.forEach { array.put(it) }
        array.toString(4)
    }

    init {
        fileHandler.read(parseEntries, { readEntries ->
            entries = readEntries
        })
    }
    companion object {
        private const val maxSize = 25
    }

    private fun updateWithNewEntry(newEntry: String) {

        val mostRecentEntry = entries.firstOrNull()

        if (mostRecentEntry != null) {
            if (newEntry.startsWith(mostRecentEntry))
                entries.removeFirst()

            else if (mostRecentEntry.startsWith(newEntry))
                return
        }

        removeEntry(newEntry)
        entries.addFirst(newEntry)
    }

    private fun ensureListNotTooBig() {
        while(entries.size > maxSize)
            entries.removeLast()
    }

    private fun removeEntry(entry: String) {
        val existingPosition = entries.indexOf(entry)
        if (existingPosition >= 0)
            entries.removeAt(existingPosition)
    }

    fun removeAt(position: Int) {
        entries.removeAt(position)
        fileHandler.write(serializeEntries)
    }

    fun read(): ArrayList<String>? {
        if (! ::entries.isInitialized)
            return null

        return ArrayList(entries)
    }


    fun push(newEntry: String) {
        if (! ::entries.isInitialized)
            return

        updateWithNewEntry(newEntry)
        ensureListNotTooBig()
        fileHandler.write(serializeEntries)
    }
}