package com.gaumala.openjisho.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File


@ExperimentalCoroutinesApi
class ChannelFileHandlerTest {
    companion object {
        val mockParse: (String) -> String = { text -> text }
    }

    @get:Rule
    var folder = TemporaryFolder()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    lateinit var testFile: File

    @Before
    fun setup() {
        testFile = folder.newFile("myFile.txt")
    }

    @After
    fun teardown() {
        testFile.delete()
    }

    private fun createFileHandler(): AsyncFileHandler {
        return AsyncFileHandler.ChannelFileHandler(
            TestCoroutineScope(),
            testCoroutineRule.testDispatchers,
            testFile.absolutePath)
    }

    @Test
    fun `read() should return the contents passed to write()`() = runBlockingTest {
        val fileHandler = createFileHandler()
        fileHandler.write { "Hello World!" }

        var writtenText = ""
        fileHandler.read(mockParse) {
            writtenText = it
        }

        writtenText `should be equal to` "Hello World!"
    }
}