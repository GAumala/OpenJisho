package com.gaumala.openjisho.utils.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.GravityCompat
import androidx.viewbinding.ViewBinding
import com.gaumala.openjisho.R
import com.gaumala.openjisho.utils.getColorFromTheme
import com.gaumala.openjisho.utils.getResIdFromTheme
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.xwray.groupie.viewbinding.GroupieViewHolder

/**
 * Custom view holder for items that can be swiped for a delete gesture.
 */
open class SwipeableViewHolder<T: ViewBinding>(b: T,
                                          rootLayout: View,
                                          private val mainView: View,
                                          private val deleteIcon: View
)
    : GroupieViewHolder<T>(b), SwipeableContainer {

    init {
        rootLayout.background =
            createCardBackground(rootLayout.context)
    }

    override fun clearView() {
        mainView.translationX = 0f
    }

    override val viewToSwipe: View = mainView

    override fun adjustToSwipeDirection(isRight: Boolean) {
        val params = deleteIcon.layoutParams as FrameLayout.LayoutParams
        val varFlag = if (isRight) GravityCompat.START else GravityCompat.END
        val newGravity = Gravity.CENTER_VERTICAL or varFlag

        if (params.gravity == newGravity)
            return

        params.gravity = newGravity
        deleteIcon.layoutParams = params
    }

    companion object {
        fun createCardBackground(ctx: Context): Drawable {
            val deleteRed = ctx.getColorFromTheme(R.attr.colorError)
            val shapeAppearance = ShapeAppearanceModel.builder(
                ctx,
                ctx.getResIdFromTheme(R.attr.shapeAppearanceMediumComponent),
                0).build()
            val bgShape = MaterialShapeDrawable(shapeAppearance)
            bgShape.fillColor = ColorStateList.valueOf(deleteRed)
            return bgShape
        }
    }
}