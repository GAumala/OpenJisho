package com.gaumala.openjisho.backend.setup.radkfile

import com.gaumala.openjisho.backend.setup.file.DictFileSamples
import com.gaumala.openjisho.common.RadicalSection
import org.amshove.kluent.`should equal`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class RadkfileParserTest {

    @get:Rule
    var folder = TemporaryFolder()

    lateinit var radkfileFile: File

    @Before
    fun setup() {
        radkfileFile = folder.newFile()
    }

    @After
    fun teardown() {
        radkfileFile.delete()
    }

    private fun createRadkfileFile() {
        radkfileFile.writeText(DictFileSamples.radkfile, charset("EUC-JP"))
    }

    @Test
    fun `it can parse a single entry`() {
        createRadkfileFile()

        val entries = ArrayList<RadicalSection>()
        RadkfileParser().exec(radkfileFile) { entry, _ ->
            entries.add(entry)
        }

        val expectedEntries = listOf(
            RadicalSection(
                radical = "力",
                strokes = 2,
                kanji = LinkedList(listOf(
                    "甥", "伽", "加", "嘉", "架", "茄", "迦", "賀", "駕", "劾", "勘", "勧", "協",
                    "脅", "勤", "筋", "勲", "袈", "功", "効", "劫", "捌", "助", "鋤", "勝", "勢",
                    "男", "勅", "努", "働", "動", "別", "勉", "募", "勃", "務", "霧", "勇", "湧",
                    "幼", "虜", "力", "励", "劣", "労", "肋", "脇", "仂", "劬", "劭", "劼", "劵",
                    "勁", "勍", "勗", "勞", "勣", "勦", "飭", "勠", "勳", "勵", "勸", "娚", "嬲",
                    "嫐", "恊", "慟", "懃", "拗", "抛", "撈", "朸", "枷", "椦", "沒", "渤", "珈",
                    "痂", "癆", "窈", "笳", "耡", "舅", "莇", "跏", "踴", "釛", "勒", "黝"))
            )
        )
        entries `should equal` expectedEntries
    }
}