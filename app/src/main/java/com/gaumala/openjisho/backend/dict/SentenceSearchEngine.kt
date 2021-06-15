package com.gaumala.openjisho.backend.dict

import com.gaumala.openjisho.backend.TatoebaQuery
import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.common.Sentence

class SentenceSearchEngine(private val dao: DictQueryDao,
                           private val pageSize: Int) {

    fun search(queryText: String, offset: Int): List<Sentence> {
        val query = TatoebaQuery.resolve(queryText) ?: throw IllegalArgumentException()
        return dao.lookupSentences(query, pageSize, offset)
    }
}