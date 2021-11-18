package com.gaumala.openjisho.frontend.history

import com.gaumala.openjisho.utils.AsyncFileHandler
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class DictHistory(private val fileHandler: AsyncFileHandler) {
    private lateinit var entries: List<String>

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

    private val serializeEntries: () -> String = {
        val array = JSONArray()
        entries.forEach { array.put(it) }
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
        val entriesCopy = LinkedList(entries)
        val mostRecentEntry = entriesCopy.firstOrNull()

        if (mostRecentEntry != null) {
            // We want to replace the most recent entry with
            // the new one if it is a prefix of the new one. So,
            // if you are typing a long word, you only get
            // the complete word added to your history. However,
            // wildcard searches should be treated as a different
            // thing, so let's make sure we can recognize those.
            if (newEntry.startsWith(mostRecentEntry)
                && newEntry != mostRecentEntry + '_'
                && newEntry != mostRecentEntry + '%'
                && newEntry != mostRecentEntry + '*')
                entriesCopy.removeFirst()

            val existingPosition = entriesCopy.indexOf(newEntry)
            if (existingPosition >= 0)
                entriesCopy.removeAt(existingPosition)
        }

        entriesCopy.addFirst(newEntry)
        entries = entriesCopy
    }

    private fun ensureListNotTooBig() {
        val entriesCopy = LinkedList(entries)
        while(entriesCopy.size > maxSize)
            entriesCopy.removeLast()
        entries = entriesCopy
    }

    fun removeAt(position: Int) {
        if (! ::entries.isInitialized) return

        val entriesCopy = LinkedList(entries)
        if (position < 0 || position >= entriesCopy.size) return

        entriesCopy.removeAt(position)
        entries = entriesCopy

        fileHandler.write(serializeEntries)
    }

    fun read(): ArrayList<String>? {
        if (! ::entries.isInitialized) return null

        return ArrayList(entries)
    }


    fun push(newEntry: String) {
        if (! ::entries.isInitialized) return

        updateWithNewEntry(newEntry)
        ensureListNotTooBig()
        fileHandler.write(serializeEntries)
    }
}