package com.gaumala.openjisho.frontend.navigation

/**
 * An interface for objects that can provide navigation. This is usually
 * implemented by activities so that fragments can easily navigate to
 * different places in the app.
 */
interface Navigator {
    /**
     * navigates to the destination that the implementation associates
     * to the provided [screen] value. It could be a fragment or a new
     * activity.
     */
    fun goTo(screen: MainScreen)
}