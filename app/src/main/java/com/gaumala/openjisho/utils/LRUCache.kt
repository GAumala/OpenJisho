package com.gaumala.openjisho.utils

// The linked hash map constructor params are the same that
// the Java Platform uses, the only difference is that
// I want access order.
abstract class LRUCache<K, V>(private val cacheSize: Int)
    : LinkedHashMap<K, V>(16, 0.75f, true) {

    abstract fun countValueSize(value: V): Int

    override fun removeEldestEntry( eldest: Map.Entry<K, V>): Boolean {
        if (size == 1)
            return false

        val calculatedSize = entries.fold(0) { countedSize, (_, v) ->
            countedSize + countValueSize(v)
        }
        return calculatedSize > cacheSize
    }
}