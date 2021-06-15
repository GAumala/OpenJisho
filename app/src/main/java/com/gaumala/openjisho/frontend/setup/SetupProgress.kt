package com.gaumala.openjisho.frontend.setup

sealed class SetupProgress {
    object Downloading : SetupProgress()
    data class Populating(val percentage: Int): SetupProgress()
}