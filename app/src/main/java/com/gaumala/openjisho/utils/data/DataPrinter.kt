package com.gaumala.openjisho.utils.data

interface DataPrinter<T> {
    fun print(value: T): String
    fun scan(printed: String): T
    val ext: String
}