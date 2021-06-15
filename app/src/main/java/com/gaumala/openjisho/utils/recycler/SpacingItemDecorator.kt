package com.gaumala.openjisho.utils.recycler

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecorator(val size: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val top = if (position == 0) size else 0
        outRect.set(0, top, 0, size)
    }

    companion object{
        fun fromDimen(ctx: Context, @DimenRes dimenResId: Int): SpacingItemDecorator {
            val size = ctx.resources.getDimension(dimenResId)
                .toInt()
            return SpacingItemDecorator(size)
        }
    }
}