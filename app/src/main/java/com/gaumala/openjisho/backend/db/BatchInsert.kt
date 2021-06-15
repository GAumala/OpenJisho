package com.gaumala.openjisho.backend.db

import java.util.*

abstract class BatchInsert<T>(private val batchLimit: Int) {

    private val buffer = LinkedList<T>()

    abstract fun runBatchInsert(batch: List<T>)

    fun insert(row: T) {
        buffer.add(row)

        if (buffer.size >= batchLimit) {
            runBatchInsert(buffer)
            buffer.clear()
        }
    }
    fun insert(rows: List<T>) {
        if (rows.isEmpty())
            return
        buffer.addAll(rows)

        if (buffer.size >= batchLimit) {
            runBatchInsert(buffer)
            buffer.clear()
        }
    }
    fun flush() {
        if (buffer.isNotEmpty()) {
            runBatchInsert(buffer)
            buffer.clear()
        }
    }
}