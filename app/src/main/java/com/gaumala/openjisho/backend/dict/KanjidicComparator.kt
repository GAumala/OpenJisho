package com.gaumala.openjisho.backend.dict

import com.gaumala.openjisho.backend.db.KanjidicRow

/**
 * Comparator class that uses the order in which kanji appear in an
 * input string. The characters that appear earlier in the string are
 * considered "less" than the ones that appear later.
 */
class KanjidicComparator(
    private val kanjiText: String
): Comparator<KanjidicRow> {
    override fun compare(left: KanjidicRow?, right: KanjidicRow?): Int {
        val leftIndex = if (left == null) -1
        else kanjiText.indexOf(left.literal)
        val rightIndex = if (right == null) -1 else
            kanjiText.indexOf(right.literal)
        if (leftIndex == -1 && rightIndex != -1) {
            return 1
        } else if (leftIndex != -1 && rightIndex == -1) {
            return -1
        } else if (leftIndex == -1 && rightIndex == -1) {
            return 0
        }
        if (leftIndex < rightIndex) {
            return -1
        } else if (leftIndex > rightIndex) {
            return 1
        }
        return 0
    }
}