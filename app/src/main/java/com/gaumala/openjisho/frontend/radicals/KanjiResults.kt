package com.gaumala.openjisho.frontend.radicals

import com.gaumala.openjisho.common.KanjiStrokesTuple
import com.gaumala.openjisho.common.UIText

sealed class KanjiResults {
    object Loading: KanjiResults()
    data class Ready(val results: List<KanjiStrokesTuple>): KanjiResults()
    data class Error(val msg: UIText): KanjiResults()
}