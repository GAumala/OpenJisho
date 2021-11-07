package com.gaumala.openjisho.backend.dict

import com.gaumala.openjisho.backend.db.DictQueryDao
import com.gaumala.openjisho.common.Sentence
import com.gaumala.openjisho.frontend.QuerySuggestion
import com.gaumala.openjisho.frontend.dict.EntryResult
import com.gaumala.openjisho.utils.LRUListCache
import com.gaumala.openjisho.utils.async.AsyncWorker
import com.gaumala.openjisho.utils.error.BetterQueriesException
import com.gaumala.openjisho.utils.error.Either
import com.gaumala.openjisho.utils.error.NotFoundException
import kotlin.math.min

/**
 * Interface for objects that can search and cache dict results.
 * All instance should use the same cache. If a value is set to an
 * arbitrary instance, that value should be available to all existent
 * and future instances.
 */
interface DictCache {
    companion object {
        const val PAGE_SIZE = 50
    }

    fun getCachedEntryResults(queryText: String): List<EntryResult>?
    fun getCachedSentenceResults(queryText: String): List<Sentence>?
    fun searchEntries(queryText: String,
                      offset: Int,
                      callback: ((Either<Exception, List<EntryResult>>) -> Unit)? = null)
    fun searchSentences(queryText: String,
                        offset: Int,
                        callback: ((Either<Exception, List<Sentence>>) -> Unit)? = null)
    fun getSuggestionsForLastEntryResults(
        queryText: String,
        lastResults: List<EntryResult.JMdict>,
        callback: ((Either<Exception, List<QuerySuggestion>>) -> Unit)? = null
    )


    class Default(private val worker: AsyncWorker, dao: DictQueryDao): DictCache {

        private companion object {
            const val MAX_CACHED_ENTRIES = 200
            const val MAX_CACHED_SENTENCES = 400

            val entryCache: LRUListCache<String, EntryResult> = LRUListCache(MAX_CACHED_ENTRIES)
            val sentenceCache: LRUListCache<String, Sentence> = LRUListCache(MAX_CACHED_SENTENCES)
        }

        private val entrySEngine = EntrySearchEngine(dao, PAGE_SIZE)
        private val sentenceSEngine = SentenceSearchEngine(dao, PAGE_SIZE)

        override fun getCachedEntryResults(queryText: String) = entryCache[queryText]

        override fun getCachedSentenceResults(queryText: String) = sentenceCache[queryText]

        private fun searchEntriesWorkload(queryText: String, offset: Int) = { ->
            val res = entrySEngine.search(queryText, offset)
            if (offset == 0 && res.isEmpty())
                throw NotFoundException()
            else
                res
        }

        private fun searchSentencesWorkload(queryText: String, offset: Int) = { ->
            val res = sentenceSEngine.search(queryText, offset)
            if (offset == 0 && res.isEmpty())
                throw NotFoundException()
            else
                res
        }

        private fun getSuggestionsWorkload(
            queryText: String,
            lastResults: List<EntryResult.JMdict>
        ) = { ->
            entrySEngine.getSuggestionsForLastEntryResults(queryText, lastResults)
        }

        private fun <T> getCachedPage(cachedList: List<T>, offset: Int): List<T> {
            return if (offset == 0 && cachedList.size <= PAGE_SIZE)
                cachedList
            else {
                val endIndex = min(offset + PAGE_SIZE, cachedList.size)
                cachedList.subList(offset, endIndex)
            }
        }

        override fun searchEntries(queryText: String,
                                   offset: Int,
                                   callback: ((Either<Exception, List<EntryResult>>) -> Unit)?) {
            val cachedEntries = getCachedEntryResults(queryText)
            if (cachedEntries!= null && offset < cachedEntries.size) {
                // CACHE HIT
                val result = getCachedPage(cachedEntries, offset)
                callback?.invoke(Either.Right(result))
                return
            }

            // Fetch a new page
            val wrappedCallback = { either: Either<Exception, List<EntryResult>> ->
                when (either) {
                    is Either.Right -> {
                        val valueToCache =
                            cachedEntries?.plus(either.value) ?: either.value
                        entryCache[queryText] = valueToCache
                    }
                    is Either.Left -> {
                        val ex = either.value
                        if (ex is BetterQueriesException) {
                            // if there are better queries, catch the exception
                            // to cache the suggestions.
                            ex.suggestions.forEach {
                                entryCache[it.queryText] = it.results
                            }
                        }
                    }
                }
                callback?.invoke(either)
                Unit
            }

            val workload = searchEntriesWorkload(queryText, offset)
            worker.workInBackground(workload, wrappedCallback)
        }

        override fun searchSentences(queryText: String,
                                     offset: Int,
                                     callback: ((Either<Exception, List<Sentence>>) -> Unit)?) {
            val cachedSentences = getCachedSentenceResults(queryText)
            if (cachedSentences!= null && offset < cachedSentences.size) {
                // CACHE HIT
                val result = getCachedPage(cachedSentences, offset)
                callback?.invoke(Either.Right(result))
                return
            }

            // Fetch a new page
            val wrappedCallback = { either: Either<Exception, List<Sentence>> ->
                if (either is Either.Right) {
                    val valueToCache =
                        cachedSentences?.plus(either.value) ?: either.value
                    sentenceCache[queryText] = valueToCache
                }
                callback?.invoke(either)
                Unit
            }

            val workload = searchSentencesWorkload(queryText, offset)
            worker.workInBackground(workload, wrappedCallback)
        }

        override fun getSuggestionsForLastEntryResults(
            queryText: String,
            lastResults: List<EntryResult.JMdict>,
            callback: ((Either<Exception, List<QuerySuggestion>>) -> Unit)?
        ) {
            // This is supposed to be called after loading the first
            // page so there has to be something cached.
            val cachedEntries = getCachedEntryResults(queryText) ?: return
            val wrappedCallback = { either: Either<Exception, List<QuerySuggestion>> ->
                if (either is Either.Right) {
                    // Cache every suggestion along with its results
                    val suggestions = either.value
                    suggestions.forEach { suggestion ->
                        entryCache[suggestion.queryText] = suggestion.results
                    }
                }
                callback?.invoke(either)
                Unit
            }

            val workload = getSuggestionsWorkload(queryText, lastResults)
            worker.workInBackground(workload, wrappedCallback)
        }
    }
}