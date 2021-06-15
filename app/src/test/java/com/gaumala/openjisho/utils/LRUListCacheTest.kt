package com.gaumala.openjisho.utils

import org.amshove.kluent.`should equal`
import org.junit.Test

class LRUListCacheTest {

    @Test
    fun `should evict after reaching capacity limit`() {
        val cache = LRUListCache<Int, Int>(10)
        for (i in 1..15)
            cache[i] = listOf(i)

        cache.keys `should equal` setOf(6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
    }
}