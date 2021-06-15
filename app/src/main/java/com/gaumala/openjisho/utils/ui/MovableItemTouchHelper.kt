package com.gaumala.openjisho.utils.ui

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView

abstract class MovableItemTouchHelper {

    private val swipeDelegate = object: SwipeableItemTouchHelper() {
        override fun onItemSwiped(itemPosition: Int) {
            this@MovableItemTouchHelper.onItemSwiped(itemPosition)
        }
    }

    private val callback = object: ItemTouchHelper.SimpleCallback(
        START or END or UP or DOWN, START or END) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder): Boolean {
            onItemMoved(viewHolder.adapterPosition,
                target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            swipeDelegate.callback.onSwiped(viewHolder, direction)
        }
        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?,
                                       actionState: Int) {
            if (actionState != ACTION_STATE_SWIPE) {
                super.onSelectedChanged(viewHolder, actionState)
                return
            }

            swipeDelegate.callback.onSelectedChanged(viewHolder, actionState)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean) {
            if (actionState != ACTION_STATE_SWIPE) {
                super.onChildDraw(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive)
                return
            }

            swipeDelegate.callback.onChildDraw(c, recyclerView,
                viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        override fun clearView(recyclerView: RecyclerView,
                               viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)

            swipeDelegate.callback.clearView(recyclerView, viewHolder)
        }
    }

    private val delegateTouchHelper by lazy {
        ItemTouchHelper(callback)
    }

    abstract fun onItemSwiped(itemPosition: Int)

    abstract fun onItemMoved(srcPosition: Int, dstPosition: Int)

    fun attachTo(recyclerView: RecyclerView?) {
        delegateTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun detach() {
        delegateTouchHelper.attachToRecyclerView(null)
    }
}