package com.gaumala.openjisho.common

/**
 * Word level index for Tatoeba sentences
 * http://edrdg.org/wiki/index.php/Sentence-Dictionary_Linking
 */
data class WordIndex(
    val headword: String?,
    val reading: String?,
    val senseNumber: Int?,
    val usedForm: String?,
    val isChecked: Boolean
) {
    val displayForm: String = headword ?: usedForm ?: reading ?: ""

    val sentenceForm: String = usedForm ?: headword ?: reading ?: ""

    companion object {
        fun buildSentence(indices: List<WordIndex>, spaces: Boolean): String {
            val separator = if (spaces) " " else ""
            return indices.joinToString(separator = separator) {
                it.sentenceForm
            }
        }
    }
}