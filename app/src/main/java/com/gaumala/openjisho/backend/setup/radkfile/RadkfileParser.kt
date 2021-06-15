package com.gaumala.openjisho.backend.setup.radkfile

import com.gaumala.openjisho.common.RadicalSection
import com.gaumala.openjisho.utils.forEachCodepoint
import java.io.File
import java.nio.charset.Charset

/**
 * Parser for the RADKFILE text file.
 *
 * Unlike [com.gaumala.openjisho.backend.setup.jmdict.JMdictParser] and
 * [com.gaumala.openjisho.backend.setup.kanjidic.KanjidicParser], this class
 * is stateful.
 */
class RadkfileParser {
    private lateinit var currentIndex: RadicalSection
    private var hasPendingData = false

    private fun parseNewRadicalIndex(line: String): RadicalSection {
        val elements = line.split(' ')
        return RadicalSection(
            radical = elements[1],
            strokes = elements[2].toInt()
        )
    }

    private fun addKanjiLine(line: String) {
        line.trim().forEachCodepoint { _, kanji ->
            currentIndex.kanji.add(kanji)
        }
    }
    /**
     * parses the provided [dictFile] and executes [callback] for
     * every parsed entry. Blocks the calling thread until the whole
     * file is parsed.
     * @param dictFile The input file. It is expected to be in the EUC-JP
     * encoding, unlike all the other dictionary files.
     */
    fun exec(dictFile: File, callback: (RadicalSection, Int) -> Unit) {
        var lineNumber = 0
        dictFile.forEachLine(Charset.forName("EUC-JP")) {line ->
            lineNumber++

            if (line.startsWith('$')) {
                if (::currentIndex.isInitialized)
                    callback(currentIndex, lineNumber)
                currentIndex = parseNewRadicalIndex(line)
                hasPendingData = false
            } else if (! line.startsWith('#')) {
                addKanjiLine(line)
                hasPendingData = true
            }
        }

        if (hasPendingData)
            callback(currentIndex, lineNumber)
    }
}