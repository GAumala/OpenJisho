package com.gaumala.openjisho.utils.ui

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeableItemTouchHelper {

    val callback = object: ItemTouchHelper.SimpleCallback(
        0, START or END) {
        /**
         * This variable is only meant to be used to keep track of items that
         * got removed with a swipe because onSwiped() is called before
         * clearView(). If item got swiped it shouldn't restore the view,
         * because it will flash before being actually removed. HOWEVER, in
         * this scenario the view MUST be restored the next time it binds
         * to a new item.
         */
        private var gotSwiped = false

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder) = false

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?,
                                       actionState: Int) {
            val holder = viewHolder as? CardViewHolder<*> ?: return
            Callback.getDefaultUIUtil().onSelected(holder.viewToSwipe)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean) {
            val holder = viewHolder as? SwipeableContainer ?: return
            Callback.getDefaultUIUtil()
                .onDraw(c, recyclerView, holder.viewToSwipe,
                    dX, dY, actionState, isCurrentlyActive)
            holder.adjustToSwipeDirection(dX >= 0)
        }

        override fun clearView(recyclerView: RecyclerView,
                               viewHolder: RecyclerView.ViewHolder) {
            val holder = viewHolder as? SwipeableContainer ?: return

            // Avoid flashing after swipe,
            // restore later when view holder rebinds
            if (gotSwiped) {
                gotSwiped = false
                return
            }

            Callback.getDefaultUIUtil()
                .clearView(holder.viewToSwipe)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                              direction: Int) {
            onItemSwiped(viewHolder.adapterPosition)
            gotSwiped = true
        }
    }

    private val delegateTouchHelper = ItemTouchHelper(callback)

    fun attachTo(recyclerView: RecyclerView?) {
        delegateTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun detach() {
        delegateTouchHelper.attachToRecyclerView(null)
    }

    abstract fun onItemSwiped(itemPosition: Int)
}