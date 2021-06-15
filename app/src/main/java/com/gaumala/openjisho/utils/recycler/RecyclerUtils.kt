package com.gaumala.openjisho.utils.recycler

import android.os.Handler
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager



fun RecyclerView.setOnScrollToBottomListener(callback: () -> Unit) {
    val llm = layoutManager as LinearLayoutManager
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        private val handler = Handler()

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState != RecyclerView.SCROLL_STATE_IDLE)
                return

            val itemCount = llm.itemCount
            val lastVisibleItem = llm.findLastVisibleItemPosition()
            if (lastVisibleItem + 1 >= itemCount)
                handler.post(callback)
        }
    })
}

fun RecyclerView.saveState(): Parcelable? {
    return layoutManager?.onSaveInstanceState()
}

fun RecyclerView.restoreState(savedState: Parcelable) {
    layoutManager?.onRestoreInstanceState(savedState)
}
