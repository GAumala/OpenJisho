package com.gaumala.openjisho.frontend.study_list

import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry

interface StudyListNavigator {
    fun gotToComposeText()
    fun goToPickDictEntry(pickSentence: Boolean)
    fun goToJMdictEntry(entry: JMdictEntry.Summarized)
    fun goToKanjidicEntry(summary: KanjidicEntry)
    fun goToTextDetail(text: String)
    fun goToSentence(japanese: String, english: String)
}