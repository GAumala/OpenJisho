package com.gaumala.openjisho.utils.image

import android.graphics.Matrix
import android.graphics.Point
import kotlin.math.min
import kotlin.math.roundToInt

interface MatrixCalculator {
    fun calculate(drawableDimens: Point, viewDimens: Point): Matrix

    class FitTop: MatrixCalculator {
        override fun calculate(drawableDimens: Point, viewDimens: Point): Matrix {
            val scale = if (drawableDimens.x <= viewDimens.x
                && drawableDimens.y <= viewDimens.y)
                1.0f
            else
                min(viewDimens.x.toFloat() / drawableDimens.x.toFloat(),
                    viewDimens.y.toFloat() / drawableDimens.y.toFloat())

            val translateX = ((viewDimens.x - drawableDimens.x * scale) * 0.5f)
                .roundToInt().toFloat()

            val newMatrix = Matrix()
            newMatrix.setScale(scale, scale)
            newMatrix.postTranslate(translateX, 0f)
            return newMatrix
        }
    }

    class FitBottom: MatrixCalculator {
        override fun calculate(drawableDimens: Point, viewDimens: Point): Matrix {
            val scale = if (drawableDimens.x <= viewDimens.x
                && drawableDimens.y <= viewDimens.y)
                1.0f
            else
                min(viewDimens.x.toFloat() / drawableDimens.x.toFloat(),
                    viewDimens.y.toFloat() / drawableDimens.y.toFloat())

            val translateX = ((viewDimens.x - drawableDimens.x * scale) * 0.5f)
                .roundToInt().toFloat()
            val translateY = (viewDimens.y - drawableDimens.y * scale)
                .roundToInt().toFloat()

            val newMatrix = Matrix()
            newMatrix.setScale(scale, scale)
            newMatrix.postTranslate(translateX, translateY)
            return newMatrix
        }
    }

    class CropTop: MatrixCalculator {
        override fun calculate(drawableDimens: Point, viewDimens: Point): Matrix {
            val translateX = ((viewDimens.x - drawableDimens.x) * 0.5f)
                .roundToInt().toFloat()

            val newMatrix = Matrix()
            newMatrix.postTranslate(translateX, 0f)
            return newMatrix
        }
    }
}