package com.gaumala.openjisho.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Sentence(val id: Long, val japanese: String, val english: String): Parcelable