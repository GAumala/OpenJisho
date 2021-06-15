package com.gaumala.openjisho.common

import android.content.Context
import androidx.annotation.StringRes

abstract class UIText {

    abstract fun getText(ctx: Context): String

     data class Resource(@StringRes val resId: Int, val params: List<Any>): UIText() {

         constructor(@StringRes resId: Int): this(resId, emptyList())

        override fun getText(ctx: Context): String {
            if (params.isEmpty())
                return ctx.getString(resId)

            return String.format(ctx.getString(resId), *params.toTypedArray())
        }
    }

    data class Literal(val str: String): UIText() {
        override fun getText(ctx: Context) = str
    }

    companion object {
        val empty = Literal("")
    }
}
