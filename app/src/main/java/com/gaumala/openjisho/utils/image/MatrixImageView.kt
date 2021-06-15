package com.gaumala.openjisho.utils.image

import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.util.AttributeSet
import android.widget.ImageView

class MatrixImageView: ImageView {
    constructor(ctx: Context): super(ctx) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        initialize()
    }

    var matrixCalculator = object: MatrixCalculator {
        override fun calculate(drawableDimens: Point, viewDimens: Point): Matrix {
            return Matrix()
        }
    }

    private fun initialize() {
        scaleType = ScaleType.MATRIX
    }

    private fun updateMatrix() {
        if (drawable == null)
            return

        val drawableDimens =
            Point(drawable.intrinsicWidth,
                drawable.intrinsicHeight)
        val viewDimens =
            Point(width - paddingLeft - paddingRight,
                height - paddingTop - paddingBottom)

        imageMatrix = matrixCalculator.calculate(
            drawableDimens = drawableDimens,
            viewDimens = viewDimens)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        updateMatrix()
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        updateMatrix()
        return super.setFrame(l, t, r, b)
    }

}
