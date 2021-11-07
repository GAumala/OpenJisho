package com.gaumala.openjisho.frontend.history

import com.gaumala.openjisho.utils.AsyncFileHandler
import io.mockk.*
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.Test
import java.util.*

class DictHistoryTest {

    private fun createDictHistoryWithList(
        fileHandler: AsyncFileHandler = mockk(relaxed = true),
        initialList: List<String>): DictHistory {
        val readCallbackSlot = slot<(LinkedList<String>) -> Unit>()
        every {
            fileHandler.read(any(), capture(readCallbackSlot))
        } just Runs
        val dictHistory = DictHistory(fileHandler)
        readCallbackSlot.captured(LinkedList(initialList))
        return dictHistory
    }

    @Test
    fun `read() returns null if fileHandler has not read contents yet`() {
        val fileHandler = mockk<AsyncFileHandler>(relaxed = true)
        val dictHistory = DictHistory(fileHandler)

        val result = dictHistory.read()
        result `should be` null
    }

    @Test
    fun `push() does nothing if fileHandler has not read contents yet`() {
        val fileHandler = mockk<AsyncFileHandler>(relaxed = true)
        val dictHistory = DictHistory(fileHandler)

        dictHistory.push("a")

        verify(inverse = true) {
            fileHandler.write(any())
        }
    }

    @Test
    fun `push() should update the entries list for the next read()`() {
        val initialList = listOf("a", "b", "c")
        val dictHistory = createDictHistoryWithList(initialList = initialList)

        dictHistory.push("d")
        dictHistory.push("e")

        val updatedList = dictHistory.read()
        updatedList `should equal` listOf("e", "d", "a", "b", "c")
    }

    @Test
    fun `push() should remove intermediate entries when incrementally pushing a long word`() {
        val initialList = listOf("楽しい")
        val dictHistory = createDictHistoryWithList(initialList = initialList)

        // Simulate user typing one kanji at a time
        dictHistory.push("改")
        dictHistory.push("改札")
        dictHistory.push("改札口")

        val updatedList = dictHistory.read()
        updatedList `should equal` listOf("改札口", "楽しい")
    }

    @Test
    fun `push() should treat wildcard queries (%) as a different thing when removing intermediate entries`() {
        val initialList = listOf("楽しい")
        val dictHistory = createDictHistoryWithList(initialList = initialList)

        // Simulate user typing one kanji at a time
        dictHistory.push("改")
        dictHistory.push("改札")
        dictHistory.push("改札口")
        // Simulate user adding a wildcard
        dictHistory.push("改札口%")

        val updatedList = dictHistory.read()
        updatedList `should equal` listOf("改札口%", "改札口", "楽しい")
    }

    @Test
    fun `push() should treat wildcard queries (_) as a different thing when removing intermediate entries`() {
        val initialList = listOf("楽しい")
        val dictHistory = createDictHistoryWithList(initialList = initialList)

        // Simulate user typing one kanji at a time
        dictHistory.push("改")
        dictHistory.push("改札")
        dictHistory.push("改札口")
        // Simulate user adding a wildcard
        dictHistory.push("改札口_")

        val updatedList = dictHistory.read()
        updatedList `should equal` listOf("改札口_", "改札口", "楽しい")
    }

    @Test
    fun `push() should treat wildcard queries (*) as a different thing when removing intermediate entries`() {
        val initialList = listOf("楽しい")
        val dictHistory = createDictHistoryWithList(initialList = initialList)

        // Simulate user typing one kanji at a time
        dictHistory.push("改")
        dictHistory.push("改札")
        dictHistory.push("改札口")
        // Simulate user adding a wildcard
        dictHistory.push("改札口*")

        val updatedList = dictHistory.read()
        updatedList `should equal` listOf("改札口*", "改札口", "楽しい")
    }

    @Test
    fun `push() should write the new list to a file`() {
        val fileHandler = mockk<AsyncFileHandler>(relaxed = true)
        val writtenDataSlot = CapturingSlot<() -> String>()
        every { fileHandler.write(capture(writtenDataSlot)) } just Runs

        val initialList = listOf("a", "b", "c")
        val dictHistory = createDictHistoryWithList(fileHandler, initialList)

        dictHistory.push("d")

        val writtenData = writtenDataSlot.captured()
        writtenData `should equal` "[\n" +
                "    \"d\",\n" +
                "    \"a\",\n" +
                "    \"b\",\n" +
                "    \"c\"\n" +
                "]"
    }
}