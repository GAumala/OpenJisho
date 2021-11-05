package com.gaumala.openjisho.frontend.dict

import android.os.Parcelable
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
import kotlinx.android.parcel.Parcelize

sealed class EntryResult: Parcelable {

    @Parcelize
    data class JMdict(val entry: JMdictEntry.Summarized): EntryResult()

    @Parcelize
    data class Kanjidic(val entry: KanjidicEntry): EntryResult()

    @Parcelize
    data class Suggestion(
        val originalQuery: String,
        val suggestedQueries: List<String>
    ): EntryResult()

}