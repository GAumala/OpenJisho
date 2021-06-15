package com.gaumala.openjisho.frontend.my_lists

import androidx.annotation.StringRes
import com.gaumala.openjisho.common.UIText

sealed class LoadedLists {
    object Loading: LoadedLists()
    data class Ready(val list: List<ListMetadata>): LoadedLists()
    data class MultiSelect(val list: List<SelectableLM>): LoadedLists()
    data class Error(val message: UIText): LoadedLists() {
        constructor(@StringRes resId: Int): this(UIText.Resource(resId))
    }
}