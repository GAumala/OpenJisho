package com.gaumala.openjisho.backend.setup.tatoeba

import com.gaumala.openjisho.common.WordIndex
import java.lang.NumberFormatException

/**
 * Object that parses Japanese indices following the syntax described in
 * http://edrdg.org/wiki/index.php/Sentence-Dictionary_Linking
 */
object TatoebaIndicesParser {
    private fun parseHeadword(indexString: String): String? {
        val endPosition = indexString.indexOfAny(headwordDelimiters)
        if (endPosition == 0)
            return null
        if (endPosition == -1)
            return indexString

        return indexString.substring(0, endPosition)
    }

    private fun parseStringBetween(indexString: String,
                                   startDelimiter: Char,
                                   endDelimiter: Char): String? {
        val startPosition = indexString.indexOf(startDelimiter)
        if (startPosition == -1)
            return null
        val endPosition = indexString.indexOf(endDelimiter)
        if (endPosition == -1)
            return null
        return indexString.substring(startPosition + 1, endPosition)
    }

    private fun parseSenseNumber(indexString: String): Int? {
        return try {
            parseStringBetween(indexString, '[', ']')?.toInt()
        } catch (ex: NumberFormatException) {
            null
        }
    }

    private fun isChecked(indexString: String) = indexString.endsWith('~')

    /**
     * Receives a string with space separated indices and
     * returns a list of [[WordIndex]] representing each parsed index.
     */
    fun parseIndices(indices: String): List<WordIndex> {
        return indices.split(' ').map {
            WordIndex(
                headword = parseHeadword(it),
                senseNumber = parseSenseNumber(it),
                usedForm = parseStringBetween(it, '{', '}'),
                reading = parseStringBetween(it, '(', ')'),
                isChecked = isChecked(it)
            )
        }.toList()
    }

    private val headwordDelimiters = charArrayOf('(', '[', '{', '|', '~')
}