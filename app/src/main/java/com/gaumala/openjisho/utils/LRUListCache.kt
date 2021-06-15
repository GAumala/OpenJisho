package com.gaumala.openjisho.utils

/**
 * A LRU cache specialized for lists. It controls the cache size by counting
 * the the total amount of items on all lists rather than simply counting the
 * number of lists
 */
class LRUListCache<K,V>(cacheSize: Int) : LRUCache<K, List<V>>(cacheSize) {

    override fun countValueSize(value: List<V>) = value.size

}