package com.gaumala.openjisho.utils.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.utils.getColorFromTheme
import kotlin.math.min

/**
 * A view that indicates the active page. Meant to be used with ViewPager
 * It has no configurable options and probably won't work well with a large
 * number of pages, but I only have one use case for this and the current
 * functionality is good enough.
 *
 * Just add this to your XML layout with width and height set to
 * WRAP_CONTENT and then change the active page by setting pageIndex
 */
class PageIndicator: View {
    private var normalDiameter: Float = 0f
    private var highlightedDiameter: Float = 0f
    private var fullDiameter: Float = 0f

    private lateinit var highlightedPaint: Paint
    private lateinit var normalPaint: Paint

    private var numberOfPages: Int = 5
    var pageIndex: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    constructor(ctx: Context): super(ctx) {
        initialize(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs) {
        initialize(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int): super(ctx, attrs, defStyleAttr) {
        initialize(ctx)
    }

    private fun initialize(ctx: Context) {
        normalDiameter = dpToPx(6)
        highlightedDiameter = dpToPx(8)
        fullDiameter = dpToPx(8)

        val color = ctx.getColorFromTheme(R.attr.colorPrimary)

        highlightedPaint = Paint()
        highlightedPaint.color = color

        normalPaint = Paint()
        normalPaint.color = color
        normalPaint.alpha = 255 / 2
    }

    // This should be in another class to avoid duplication,
    // but R8 will probably just inline this if I leave it here.
    private fun dpToPx(dps: Int): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dps.toFloat(), resources.displayMetrics)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val startEndPadding = paddingStart + paddingEnd
        val neededWidth = fullDiameter.toInt() * numberOfPages
                            + startEndPadding
        val myMeasuredWidth = when (widthMode) {
            MeasureSpec.UNSPECIFIED -> neededWidth
            MeasureSpec.AT_MOST -> min(neededWidth, widthSize)
            else -> widthSize
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val topBottomPadding = paddingTop + paddingBottom
        val neededHeight = fullDiameter.toInt()
                            + topBottomPadding
        val myMeasuredHeight = when (heightMode) {
            MeasureSpec.UNSPECIFIED -> neededHeight
            MeasureSpec.AT_MOST -> min(neededHeight, heightSize)
            else -> heightSize
        }

        setMeasuredDimension(myMeasuredWidth, myMeasuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val neededWidth = numberOfPages * fullDiameter
        val neededHeight = fullDiameter.toInt()
        val startX = (width - paddingStart - paddingEnd - neededWidth) / 2
        val startY = (height - paddingTop - paddingBottom - neededHeight) / 2

        val highlightedRadius = highlightedDiameter / 2
        val normalRadius = normalDiameter / 2
        val circlePosY = startY + fullDiameter / 2
        var circlePosX = startX + fullDiameter / 2
        var index = 0

        while (index < numberOfPages) {
            val isHighlighted = index == pageIndex
            val radius = if (isHighlighted) highlightedRadius
                         else normalRadius
            val paint = if (isHighlighted) highlightedPaint
                        else normalPaint
            canvas.drawCircle(circlePosX, circlePosY, radius, paint)

            circlePosX += fullDiameter
            index += 1
        }
    }
}