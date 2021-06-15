package com.gaumala.openjisho.frontend.dict.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.DictLoadingItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class LoadingItem: BindableItem<DictLoadingItemBinding>(0) {

    override fun bind(viewBinding: DictLoadingItemBinding, position: Int) {
        val progressView = viewBinding.progressCircular
        progressView.visibility = View.INVISIBLE

        // Delay showing the progress bar so that it is never shown for
        // really fast queries.
        progressView.postDelayed({
            progressView.visibility = View.VISIBLE
        }, PROGRESS_DELAY)
    }

    override fun getLayout() = R.layout.dict_loading_item

    override fun equals(other: Any?): Boolean {
        return other is LoadingItem
    }

    override fun hashCode(): Int {
        return 1
    }

    override fun initializeViewBinding(view: View) =
        DictLoadingItemBinding.bind(view)

    private companion object {
        const val PROGRESS_DELAY = 150L
    }

}

