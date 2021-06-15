package com.gaumala.openjisho.frontend.dict

import android.view.View
import androidx.viewpager.widget.PagerAdapter
import com.gaumala.openjisho.R
import java.lang.IndexOutOfBoundsException

class DictPagerAdapter(private val wordsRecycler: View,
                       private val sentencesRecycler: View): PagerAdapter() {
    private val ctx = wordsRecycler.context
    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun getCount() = 2

    override fun instantiateItem(container: View, position: Int) =
        when (position) {
            0 -> wordsRecycler
            1 -> sentencesRecycler
            else -> throw IndexOutOfBoundsException()
        }

    override fun getPageTitle(position: Int): CharSequence? {
        val resId = when(position) {
            0 -> R.string.entries
            1 -> R.string.sentences
            else -> throw IndexOutOfBoundsException()
        }
        return ctx.getString(resId)
    }
}