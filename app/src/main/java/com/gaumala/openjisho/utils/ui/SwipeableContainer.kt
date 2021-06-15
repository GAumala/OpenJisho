package com.gaumala.openjisho.utils.ui

import android.view.View

/**
 * Interface for objects that contain multiple views and among those
 * a particular view that can be swiped for a delete gesture. This is designed
 * to be used by SwipeableItemTouchHelper and similar classes. It
 * has methods to keep track of the view's state and restore it to the
 * initial value after the gesture is cancelled or completed.
 */
interface SwipeableContainer {
    /**
     * The actual view that is going to be swiped.
     */
    val viewToSwipe: View

    /**
     * Restores the swipeable view's X translation and elevation. It is
     * meant to be used by SwipeableItemTouchHelper in the clearView()
     * method. It must also be called when it binds to a new item
     * after recycling so that any pending restorations get executed.
     */
    fun clearView()

    /**
     * This method is called so that any views behind the swiped view
     * can react to the on the direction of the swipe before they become
     * visible. For example a delete icon chould be displayed at the
     * left edge of the background when it is swiped to the right, or
     * at the right edge when it is swiped to the left.
     *
     * Be aware that this is called multiple throughout the whole
     * action, not just at the beginning. User can change direction
     * at any point.
     */
    fun adjustToSwipeDirection(isRight: Boolean)
}