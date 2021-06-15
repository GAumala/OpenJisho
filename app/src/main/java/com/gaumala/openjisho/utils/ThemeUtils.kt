package com.gaumala.openjisho.utils

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.annotation.StyleRes
import android.content.Context
import android.util.TypedValue


fun LayoutInflater.cloneWithTheme(@StyleRes themeResId: Int): LayoutInflater =
    cloneInContext(ContextThemeWrapper(context, themeResId))

fun Context.getColorFromTheme(attrResId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrResId, typedValue, true)
    return typedValue.data
}

fun Context.getResIdFromTheme(attrResId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrResId, typedValue, true)
    return typedValue.resourceId
}
