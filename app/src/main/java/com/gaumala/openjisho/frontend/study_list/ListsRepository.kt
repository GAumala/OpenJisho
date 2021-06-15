package com.gaumala.openjisho.frontend.study_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gaumala.openjisho.backend.lists.ListsDao
import com.gaumala.openjisho.utils.error.Either
import com.gaumala.openjisho.utils.async.AsyncWorker

class ListsRepository(private val dao: ListsDao,
                      private val worker: AsyncWorker) {

    private val newListLD = MutableLiveData<Either<Exception, String>>()

    private fun createNewListWorkload(name: String) = { ->
        dao.createNewList(name)
        name
    }

    private val updateNewListLD = { res: Either<Exception, String> ->
        newListLD.value = res
    }

    fun getNewListLiveData(): LiveData<Either<Exception, String>> {
        return newListLD
    }

    fun createNewList(name: String) {
        worker.workInBackground(createNewListWorkload(name), updateNewListLD)

    }
}