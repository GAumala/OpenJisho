package com.gaumala.openjisho.frontend.study_list.recycler

import android.view.View
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.ScLoadingItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class SCLoadingItem: BindableItem<ScLoadingItemBinding>() {

    override fun bind(viewBinding: ScLoadingItemBinding, position: Int) {
    }

    override fun getLayout() = R.layout.sc_loading_item

    override fun initializeViewBinding(view: View) =
        ScLoadingItemBinding.bind(view)
}