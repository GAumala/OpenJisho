package com.gaumala.openjisho.frontend.my_lists

import com.gaumala.openjisho.backend.lists.ListsDao
import com.gaumala.openjisho.utils.async.AsyncWorker
import com.gaumala.openjisho.utils.error.Either

/**
 * Interface for objects that can retrieve and cache the user's lists.
 * All instance should use the same cache. If a value is set to an
 * arbitrary instance, that value should be available to all existent
 * and future instances.
 */
interface MyListsCache {
   fun getCachedLists(): List<ListMetadata>?
   fun invalidate()
   fun loadLists(callback: ((Either<Exception, List<ListMetadata>>) -> Unit)? = null)

   fun preload() {
      if (getCachedLists() == null)
         loadLists()
   }

   fun reload() {
      invalidate()
      loadLists()
   }

   class Default(private val worker: AsyncWorker,
                 private val dao: ListsDao): MyListsCache {

       companion object {
          var cachedLists: List<ListMetadata>? = null
             private set
       }

      private val getAllMetadataWorkload = { ->
         dao.getAllMetadata()
      }

      override fun getCachedLists() = cachedLists

      override fun invalidate() {
         cachedLists = null
      }

      override fun loadLists(callback: ((Either<Exception, List<ListMetadata>>) -> Unit)?) {
         val wrappedCallback = { either: Either<Exception, List<ListMetadata>> ->
            if (either is Either.Right) {
               cachedLists = either.value
            }
            callback?.invoke(either)
            Unit
         }
         worker.workInBackground(getAllMetadataWorkload, wrappedCallback)
      }
   }
}
