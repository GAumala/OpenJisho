package com.gaumala.openjisho.backend.setup.tatoeba

import com.gaumala.openjisho.common.WordIndex
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Test

class TatoebaIndicesParserTest {

    @Test
    fun `should parse one example and rebuild the sentence`() {
        val exampleSentence = "その家はかなりぼろ屋になっている"
        val exampleIndices = "其の[01]{その} 家(いえ)[01] は 可也{かなり} ぼろ屋[01]~ になる[01]{になっている}"
        val parsed = TatoebaIndicesParser.parseIndices(exampleIndices)

        parsed `should equal` listOf(
            WordIndex(
                headword = "其の",
                reading = null,
                senseNumber = 1,
                usedForm = "その",
                isChecked = false
            ),
            WordIndex(
                headword = "家",
                reading = "いえ",
                senseNumber = 1,
                usedForm = null,
                isChecked = false
            ),
            WordIndex(
                headword = "は",
                reading = null,
                senseNumber = null,
                usedForm = null,
                isChecked = false
            ),
            WordIndex(
                headword = "可也",
                reading = null,
                senseNumber = null,
                usedForm = "かなり",
                isChecked = false
            ),
            WordIndex(
                headword = "ぼろ屋",
                reading = null,
                senseNumber = 1,
                usedForm = null,
                isChecked = true
            ),
            WordIndex(
                headword = "になる",
                reading = null,
                senseNumber = 1,
                usedForm = "になっている",
                isChecked = false
            )
        )

        val rebuiltSentence = WordIndex.buildSentence(parsed, false)
        rebuiltSentence `should be equal to` exampleSentence
    }
}