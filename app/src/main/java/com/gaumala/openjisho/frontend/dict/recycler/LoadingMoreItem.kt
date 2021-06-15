package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.LoadingMoreItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class LoadingMoreItem: BindableItem<LoadingMoreItemBinding>(0) {
    override fun bind(viewBinding: LoadingMoreItemBinding, position: Int) {
    }

    override fun getLayout() = R.layout.loading_more_item

    override fun equals(other: Any?): Boolean {
        return other is LoadingMoreItem
    }

    override fun hashCode(): Int {
        return 1
    }

    override fun initializeViewBinding(view: View) =
        LoadingMoreItemBinding.bind(view)
}
