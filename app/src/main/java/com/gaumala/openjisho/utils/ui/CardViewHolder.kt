package com.gaumala.openjisho.utils.ui

import android.view.View
import androidx.cardview.widget.CardView
import androidx.viewbinding.ViewBinding

/**
 * Custom view holder for items that display a CardView that can
 * be swiped for a delete gesture.
 */
class CardViewHolder<T: ViewBinding>(b: T,
                                     rootLayout: View,
                                     private val card: CardView,
                                     deleteIcon: View)
    : SwipeableViewHolder<T>(b, rootLayout, card, deleteIcon) {

    private val initialElevation = card.cardElevation

    override fun clearView() {
        card.translationX = 0f
        card.cardElevation = initialElevation
    }
}

