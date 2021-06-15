package com.gaumala.openjisho.frontend.navigation

/**
 * Interface for an Activity that contains a navigation drawer
 * so that hosted fragments can open/close the drawer. The
 * drawer is locked by default. Any fragment that wants to
 * use it, must explicitly unlock it during its lifetime and
 * lock it again when it is destroyed.
 */
interface NavDrawerContainer {
    /**
     * Fragments that include a hamburguer icon on the Top Bar
     * should call this method to open the navigation drawer.
     * Just make sure that the drawer is unlocked.
     */
    fun openDrawer()

    /**
     * Drawer is locked by default. Any fragment that wants
     * to use it must call setDrawerLocked(false) on start
     * and setDrawerLocked(true) on stop.
     *
     * Maybe this could be easier with a Lifecycle instance?
     */
    fun setDrawerLocked(isLocked: Boolean)
}