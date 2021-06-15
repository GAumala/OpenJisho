package com.gaumala.openjisho.frontend.history

import com.gaumala.openjisho.utils.AsyncFileHandler
import io.mockk.*
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.Test
import java.util.*

class DictHistoryTest {

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
    fun `read() returns null if fileHandler has not read contents yet`() {
        val fileHandler = mockk<AsyncFileHandler>(relaxed = true)
        val dictHistory = DictHistory(fileHandler)

        val result = dictHistory.read()
        result `should be` null
    }

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
    fun `push() should update the entries list for the next read()`() {
        val initialList = listOf("a", "b", "c")
        val dictHistory = createDictHistoryWithList(initialList = initialList)

        dictHistory.push("d")

        val updatedList = dictHistory.read()
        updatedList `should equal` listOf("d", "a", "b", "c")
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