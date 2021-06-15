package com.gaumala.openjisho.frontend.radicals.recycler

import android.view.View
import android.view.ViewGroup
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.StrokesHeaderBinding
import com.xwray.groupie.viewbinding.BindableItem

class StrokesHeader (private val strokesCount: Int,
                     private val isLarge: Boolean = false)
    : BindableItem<StrokesHeaderBinding>(strokesCount.toLong()) {

    override fun bind(viewBinding: StrokesHeaderBinding, position: Int) {
        viewBinding.strokesText.text = strokesCount.toString()
    }

    override fun getLayout() = R.layout.strokes_header

    override fun equals(other: Any?): Boolean {
        if (other !is StrokesHeader)
            return false

        return other.strokesCount == strokesCount
    }

    override fun hashCode(): Int {
        return strokesCount
    }

    override fun initializeViewBinding(view: View): StrokesHeaderBinding {
        val binding = StrokesHeaderBinding.bind(view)
        if (isLarge) {
            val resources = binding.root.context.resources
            val size = resources
                .getDimension(R.dimen.grid_item_large_size)
                .toInt()
            val layoutParams = ViewGroup.LayoutParams(size, size)

            val gridItem = binding.gridItem
            gridItem.layoutParams = layoutParams
        }
        return binding
    }
}