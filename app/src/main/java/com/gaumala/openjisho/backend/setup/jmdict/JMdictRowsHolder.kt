package com.gaumala.openjisho.backend.setup.jmdict

import com.gaumala.openjisho.backend.db.EngKeywordRow
import com.gaumala.openjisho.backend.db.JMdictRow
import com.gaumala.openjisho.backend.db.JpnKeywordRow
import com.gaumala.openjisho.backend.db.TagRow

data class JMdictRowsHolder(
    val jmDictRow: JMdictRow,
    val jpnKeywordRows: List<JpnKeywordRow>,
    val engKeywordRow: EngKeywordRow,
    val tagRows: List<TagRow>)