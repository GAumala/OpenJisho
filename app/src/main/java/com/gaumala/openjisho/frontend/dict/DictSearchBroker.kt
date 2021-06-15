package com.gaumala.openjisho.frontend.dict

import com.gaumala.mvi.ActionSink
import com.gaumala.openjisho.R
import com.gaumala.openjisho.backend.dict.DictCache
import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.frontend.dict.actions.PostSearchResults
import com.gaumala.openjisho.utils.async.MessageThrottler
import com.gaumala.openjisho.utils.error.Either
import com.gaumala.openjisho.utils.error.NotFoundException

/**
 * A class that calls [DictCache] for dictionary queries and handles
 * the result submitting the appropriate action to the [ActionSink] so
 * that it can compute the next [DictState].
 *
 * This class is designed with throttling in mind, as we want to delay query
 * execution until the user finishes typing. For that matter, it extends
 * [MessageThrottler.Receiver] and only runs a query after a throttled
 * message is received.
 *
 * Because of these reasons [DictFragment] must call this class instead of
 * calling [DictCache] or anything in the [com.gaumala.openjisho.backend] package.
 */
class DictSearchBroker(private val cache: DictCache)
    : MessageThrottler.Receiver<DictSearchMsg> {

    private fun hasMoreData(totalLoaded: Int): Boolean {
        return totalLoaded == DictCache.PAGE_SIZE
    }

    private fun searchEntries(sink: ActionSink<DictState, DictSideEffect>,
                              queryText: String,
                              offset: Int) {
        cache.searchEntries(queryText, offset) {
            val result = when (it) {
                is Either.Right ->
                    DictSearchResult.Entries(
                        queryText,
                        hasMoreData(it.value.size),
                        it.value)
                is Either.Left ->
                    DictSearchResult.Error(
                        queryText,
                        exceptionToMessage(queryText, it.value),
                        isSentence = false
                    )
            }
            sink.submitAction(PostSearchResults(result))
        }
    }

    private fun searchSentences(sink: ActionSink<DictState, DictSideEffect>,
                                queryText: String,
                                offset: Int) {
        cache.searchSentences(queryText, offset) {
            val result = when (it) {
                is Either.Right ->
                    DictSearchResult.Sentences(
                        queryText,
                        hasMoreData(it.value.size),
                        it.value
                    )
                is Either.Left ->
                    DictSearchResult.Error(
                        queryText,
                        exceptionToMessage(queryText, it.value),
                        isSentence = true
                    )
            }
            sink.submitAction(PostSearchResults(result))
        }
    }

    override fun handleMessage(msg: DictSearchMsg) {
        val params = msg.params
            if (params.lookupSentences)
                searchSentences(msg.sink, params.queryText, params.offset)
            else
                searchEntries(msg.sink, params.queryText, params.offset)
    }

    private fun exceptionToMessage(queryText: String,
                                   exception: Exception): UIText {
        return when (exception) {
            is IllegalArgumentException ->
                UIText.Resource(R.string.not_valid_query, listOf(queryText))
            is NotFoundException ->
                UIText.Resource(R.string.not_found, listOf(queryText))
            else ->
                UIText.Resource(R.string.something_wrong)
        }
    }
}