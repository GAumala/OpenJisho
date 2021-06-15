package com.gaumala.openjisho.frontend.sentence

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.db.JMdictRow
import com.gaumala.openjisho.common.JMdictEntry
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should equal`
import org.junit.Test

class WordSearchEngineTest {
    @Test
    fun `should search the entries listed by the indices`() {
        val dao = mockk<DictQueryDao>()
        every {
            dao.lookupJMdictRowsExact("の")
        } returns listOf(
            JMdictRow(id = 1001, entryJson = """{
                |"kanji":[],
                |"reading":[{"text":"の","tags":[]}],
                |"sense":[{"glossItems":["indicates possessive"],"glossTags":["prt"]}]
                |}""".trimMargin())
        )
        every {
            dao.lookupJMdictRowsExact("田中")
        } returns emptyList()
        every {
            dao.lookupJMdictRowsExact("家")
        } returns listOf(
            JMdictRow(id = 1002, entryJson = """{
                |"kanji":[{"text":"家","tags":[]}],
                |"reading":[{"text":"いえ","tags":[]}],
                |"sense":[{"glossItems":["house","residence"],
                    |"glossTags":["prt"]}]
                |}""".trimMargin()),
            JMdictRow(id = 1003, entryJson = """{
                |"kanji":[{"text":"家","tags":[]}],
                |"reading":[{"text":"うち","tags":[]}],
                |"sense":[{"glossItems":["home (one's own)"],
                    |"glossTags":["n","adj-no"]}]
                |}""".trimMargin())
        )
        every {
            dao.lookupJMdictRowsExact("に")
        } returns listOf(
            JMdictRow(id = 1004, entryJson = """{
                |"kanji":[{"text":"二","tags":["nf01"]}],
                |"reading":[{"text":"に","tags":["nf01"]}],
                |"sense":[{"glossItems":["two"],"glossTags":["num"]}]
                |}""".trimMargin()),
            JMdictRow(id = 1005, entryJson = """{
                |"kanji":[],
                |"reading":[{"text":"に","tags":[]}],
                |"sense":[{"glossItems":["indicates location"],
                    |"glossTags":["prt"]}]
                |}""".trimMargin())
        )
        every {
            dao.lookupJMdictRowsExact("居る")
        } returns listOf(
            JMdictRow(id = 1006, entryJson = """{
                |"kanji":[{"text":"居る","tags":[]}],
                |"reading":[{"text":"いる","tags":[]}],
                |"sense":[{"glossItems":["to be","to exist"],
                    |"glossTags":["v1","vi"]}]
                |}""".trimMargin()),
            JMdictRow(id = 1007, entryJson = """{
                |"kanji":[{"text":"居る","tags":["nf19"]}],
                |"reading":[{"text":"おる","tags":["nf19"]}],
                |"sense":[{"glossItems":["to be","to exist"],
                    |"glossTags":["v5r","vi"]}]
                |}""".trimMargin())
        )

        val expectedWords = listOf(
            SentenceWord.Unknown("田中"),
            SentenceWord.JMdict(
                JMdictEntry.Summarized(
                    header = "の",
                    furigana = null,
                    sub = "indicates possessive",
                    entry = JMdictEntry(
                        entryId = 1001,
                        kanjiElements = emptyList(),
                        readingElements = listOf(
                            JMdictEntry.Element(
                                text = "の",
                                tags = emptyList()
                            )
                        ),
                        senseElements = listOf(
                            JMdictEntry.Sense(
                                glossItems = listOf("indicates possessive"),
                                glossTags = listOf(
                                    JMdictEntry.Tag.parse("prt")
                                )
                            )
                        )
                    )
                )
            ), SentenceWord.JMdict(
                JMdictEntry.Summarized(
                    header = "家",
                    furigana = "いえ",
                    sub = "house; residence",
                    entry = JMdictEntry(
                        entryId = 1002,
                        kanjiElements = listOf(
                            JMdictEntry.Element(
                                text = "家",
                                tags = emptyList()
                            )
                        ),
                        readingElements = listOf(
                            JMdictEntry.Element(
                                text = "いえ",
                                tags = emptyList()
                            )
                        ),
                        senseElements = listOf(
                            JMdictEntry.Sense(
                                glossItems = listOf("house", "residence"),
                                glossTags = listOf(
                                    JMdictEntry.Tag.parse("prt")
                                )
                            )
                        )
                    )
                )
            ), SentenceWord.JMdict(
                JMdictEntry.Summarized(
                    header = "に",
                    furigana = null,
                    sub = "indicates location",
                    entry = JMdictEntry(
                        entryId = 1005,
                        kanjiElements = emptyList(),
                        readingElements = listOf(
                            JMdictEntry.Element(
                                text = "に",
                                tags = emptyList()
                            )
                        ),
                        senseElements = listOf(
                            JMdictEntry.Sense(
                                glossItems = listOf("indicates location"),
                                glossTags = listOf(
                                    JMdictEntry.Tag.parse("prt")
                                )
                            )
                        )
                    )
                )
            ) , SentenceWord.JMdict(
                JMdictEntry.Summarized(
                    header = "居る",
                    furigana = "いる",
                    sub = "to be; to exist",
                    entry = JMdictEntry(
                        entryId = 1006,
                        kanjiElements = listOf(
                            JMdictEntry.Element(
                                text = "居る", tags =
                                emptyList()
                            )
                        ),
                        readingElements = listOf(
                            JMdictEntry.Element(
                                text = "いる",
                                tags = emptyList()
                            )
                        ),
                        senseElements = listOf(
                            JMdictEntry.Sense(
                                glossItems = listOf("to be","to exist"),
                                glossTags = listOf(
                                    JMdictEntry.Tag.parse("v1"),
                                    JMdictEntry.Tag.parse("vi")
                                )
                            )
                        )
                    )
                )
            )
        )
        val indices = "田中 の 家(いえ)[01] に 居る(いる)[01]{いる}"
        val searchEngine = WordSearchEngine(dao)

        val words = searchEngine.findSentenceWords(indices)
        words `should equal` expectedWords
    }
}