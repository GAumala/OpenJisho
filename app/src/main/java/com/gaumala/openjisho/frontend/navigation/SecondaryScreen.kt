package com.gaumala.openjisho.frontend.navigation

import java.lang.IllegalArgumentException

/**
 * An enumeration of the fragments that can be displayed in
 * [com.gaumala.openjisho.SecondaryActivity]
 */
enum class SecondaryScreen {
    showEntry, pickDictEntry, composeText, showAppInfo, showSentence, showText, showHelp;

    fun toScreenKey() = when (this) {
        showHelp -> "showHelp"
        showEntry -> "showEntry"
        pickDictEntry -> "pickDictEntry"
        composeText -> "composeText"
        showAppInfo -> "showAppInfo"
        showText -> "showText"
        showSentence -> "showSentence"
    }

    fun toRequestCode() = when (this) {
        showEntry -> 1
        pickDictEntry -> 2
        composeText -> 3
        showAppInfo -> 4
        showText -> 5
        showSentence -> 6
        showHelp -> 7
    }

    companion object {
        fun fromRequestCode(int: Int) = when(int) {
            1 -> showEntry
            2 -> pickDictEntry
            3 -> composeText
            4 -> showAppInfo
            5 -> showText
            6 -> showSentence
            7 -> showHelp
            else -> throw IllegalArgumentException("Unknown request code: $int")
        }

        fun fromScreenKey(key: String) = when(key) {
            "showEntry" -> showEntry
            "pickDictEntry" -> pickDictEntry
            "composeText" -> composeText
            "showAppInfo" -> showAppInfo
            "showText" -> showText
            "showSentence" -> showSentence
            "showHelp" -> showHelp
            else -> throw IllegalArgumentException("Unknown screen key: $key")
        }
    }
}