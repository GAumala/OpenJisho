package com.gaumala.openjisho.backend.setup.kanjidic

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gaumala.openjisho.backend.setup.file.DictFileSamples
import com.gaumala.openjisho.common.KanjidicEntry
import org.amshove.kluent.`should equal`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class KanjidicParserTest {

    @get:Rule
    var folder = TemporaryFolder()

    lateinit var kanjidicFile: File

    @Before
    fun setup() {
        kanjidicFile = folder.newFile()
    }

    @After
    fun teardown() {
        kanjidicFile.delete()
    }

    private fun createKanjidicFile() {
        kanjidicFile.writeText(DictFileSamples.kanjidic)
    }

    @Test
    fun `it can parse a single entry`() {
        createKanjidicFile()

        val entries = ArrayList<KanjidicEntry>()
        KanjidicParser.exec(kanjidicFile) { entry, _ ->
            entries.add(entry)
        }

        val expectedEntries = listOf(
            KanjidicEntry(
                literal = "握",
                grade = 8,
                jlpt = 1,
                strokeCount = 12,
                meanings = listOf("grip", "hold", "mould sushi", "bribe"),
                onReadings = listOf("アク"),
                kunReadings = listOf("にぎ.る")
            )
        )
        entries `should equal` expectedEntries
    }
}