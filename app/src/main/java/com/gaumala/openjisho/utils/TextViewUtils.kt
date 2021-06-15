package com.gaumala.openjisho.utils

import android.widget.TextView

/**
 * Should be called on TextViews inside a recycler view because
 * for some reason they lose the selectable text property when
 * they are recycled. https://stackoverflow.com/a/56224791
 */
fun TextView.makeTextSelectable() {
    setTextIsSelectable(false)
    measure(-1, -1)
    setTextIsSelectable(true)
}