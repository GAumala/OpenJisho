package com.gaumala.openjisho.frontend.study_list

import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.backend.lists.ListsDao
import com.gaumala.openjisho.common.StudyCard
import com.gaumala.openjisho.frontend.study_list.actions.Read
import com.gaumala.openjisho.utils.error.Either
import com.gaumala.openjisho.utils.async.AsyncWorker

class StudyListSERunner(private val worker: AsyncWorker,
                        private val listsDao: ListsDao,
                        private val listName: String)
    : SideEffectRunner<StudyListState, StudyListSideEffect> {

    override fun runSideEffect(
        sink: ActionSink<StudyListState, StudyListSideEffect>,
        sideEffect: StudyListSideEffect) {

        if (sideEffect is StudyListSideEffect.Read)
            readFile(sink, sideEffect)

        if (sideEffect is StudyListSideEffect.Write)
            writeFile(sink, sideEffect)
    }

    private val readList = { -> listsDao.getListByName(listName) }
    private fun writeList(list: List<StudyCard>) = { ->
        listsDao.updateList(listName, list)
    }

    private fun readFile(
        sink: ActionSink<StudyListState, StudyListSideEffect>,
        sideEffect: StudyListSideEffect.Read) {

        worker.workInBackground(readList, { res ->
            val items = when (res) {
                is Either.Left -> null
                is Either.Right -> res.value
            }
            sink.submitAction(Read(items))
        })
    }

    private fun writeFile(
        sink: ActionSink<StudyListState, StudyListSideEffect>,
        sideEffect: StudyListSideEffect.Write) {

        val items = sideEffect.items
        worker.workInBackground(writeList(items)) {
            // ignore result
        }
    }
}