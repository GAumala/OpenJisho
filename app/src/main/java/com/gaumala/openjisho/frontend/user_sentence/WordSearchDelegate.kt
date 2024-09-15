package com.gaumala.openjisho.frontend.user_sentence

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.backend.setup.tatoeba.TatoebaIndicesParser
import com.gaumala.openjisho.frontend.dict.WordSearchMsg
import com.gaumala.openjisho.frontend.sentence.WordSearchEngine
import com.gaumala.openjisho.frontend.user_sentence.actions.LoadWords
import com.gaumala.openjisho.utils.async.MessageThrottler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * This class searches for sentence words in the database implementing [MessageThrottler.Receiver]
 * So that we can throttle searches.
 */
class WordSearchDelegate(dao: DictQueryDao): MessageThrottler.Receiver<WordSearchMsg> {
    private val engine = WordSearchEngine(dao)

    override suspend fun handleMessage(msg: WordSearchMsg) {
        val (indices, words) = withContext(Dispatchers.IO) {
            val indices = TatoebaIndicesParser.parseIndices(msg.sentence)
            val words = engine.findSentenceWords(indices)
            Pair(indices, words)
        }

        msg.sink.submitAction(LoadWords(msg.sentence, indices, words))
    }
}