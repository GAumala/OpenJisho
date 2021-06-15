package com.gaumala.openjisho.frontend.navigation

import com.gaumala.openjisho.frontend.dict.DictSavedState

/**
 * Classes that represent the main destinations where the user can navigate to.
 */
sealed class MainScreen {
    object Setup: MainScreen()
    object About: MainScreen()
    data class Tour(val isRunningSetup: Boolean): MainScreen()
    data class Dictionary(val savedState: DictSavedState? = null,
                          val reverse: Boolean = false): MainScreen()
    data class MyLists(val savedState: DictSavedState?,
                       val reverse: Boolean = false): MainScreen()
}