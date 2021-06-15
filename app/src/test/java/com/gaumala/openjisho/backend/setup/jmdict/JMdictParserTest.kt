package com.gaumala.openjisho.backend.setup.jmdict

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gaumala.openjisho.backend.setup.file.DictFileSamples
import com.gaumala.openjisho.common.JMdictEntry
import org.amshove.kluent.`should equal`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class JMdictParserTest {

    @get:Rule
    var folder = TemporaryFolder()

    lateinit var jmdictFile: File

    @Before
    fun setup() {
        jmdictFile = folder.newFile()
    }

    @After
    fun teardown() {
        jmdictFile.delete()
    }

    private fun createJMdictFile() {
        jmdictFile.writeText(DictFileSamples.jmdict)
    }

    @Test
    fun `it can parse a single entry`() {
        createJMdictFile()

        val entries = ArrayList<JMdictEntry>()
        JMdictParser.exec(jmdictFile) { entry, _ ->
            entries.add(entry)
        }

        val expectedEntries = listOf(
            JMdictEntry(
                entryId = 1499320,
                kanjiElements = listOf(
                    JMdictEntry.Element(
                        text = "部屋",
                        tags = listOf(
                            JMdictEntry.Tag.Parametrized(
                                entity = JMdictEntry.Entity.numberFreq,
                                param = 1000,
                                raw = "nf02"
                            )
                        ))),
                readingElements = listOf(
                    JMdictEntry.Element(
                        text = "へや",
                        tags = listOf(
                            JMdictEntry.Tag.Parametrized(
                                entity = JMdictEntry.Entity.numberFreq,
                                param = 1000,
                                raw = "nf02"
                            )
                        ))),
                senseElements = listOf(
                    JMdictEntry.Sense(
                        glossItems = listOf("room"),
                        glossTags = listOf(
                            JMdictEntry.Tag.Simple(
                                entity = JMdictEntry.Entity.noun,
                                raw = "n")
                        )
                    ),
                    JMdictEntry.Sense(
                        glossItems = listOf("stable"),
                        glossTags = listOf(
                            JMdictEntry.Tag.Simple(
                                entity = JMdictEntry.Entity.sumo,
                                raw = "sumo"),
                            JMdictEntry.Tag.Simple(
                                entity = JMdictEntry.Entity.abbreviation,
                                raw = "abbr")
                        )
                    )
                )
            )
        )
        entries `should equal` expectedEntries
    }
}