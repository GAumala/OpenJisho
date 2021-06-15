package com.gaumala.openjisho.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KanjidicEntry(val literal: String,
                         val grade: Int,
                         val jlpt: Int,
                         val strokeCount: Int,
                         val meanings: List<String>,
                         val onReadings: List<String>,
                         val kunReadings: List<String>): Parcelable