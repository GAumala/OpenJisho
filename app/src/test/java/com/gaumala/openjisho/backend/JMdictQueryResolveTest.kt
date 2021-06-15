package com.gaumala.openjisho.backend

import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldBeNull
import org.junit.Test

class JMdictQueryResolveTest {
    @Test
    fun `should return null with empty query text`() {
        val query = JMdictQuery.resolve("")

        query.shouldBeNull()
    }

    @Test
    fun `should return null with queries that have nothing but wildcards and spaces`() {
        val queries = listOf(
            JMdictQuery.resolve("*"),
            JMdictQuery.resolve("%"),
            JMdictQuery.resolve("_"),
            JMdictQuery.resolve(" "),
            JMdictQuery.resolve(" ** %_%")
        )

        queries `should equal` listOf(null, null, null, null, null)
    }

    @Test
    fun `should return Match with queries that contain wildcards`() {
        val queries = listOf(
            JMdictQuery.resolve("*手"),
            JMdictQuery.resolve("入%"),
            JMdictQuery.resolve("複_"),
            JMdictQuery.resolve("*出%")
        )

        queries `should equal` listOf(
            JMdictQuery.Like("%手"),
            JMdictQuery.Like("入%"),
            JMdictQuery.Like("複_"),
            JMdictQuery.Like("%出%")
        )
    }

    @Test
    fun `should return Exact with queries that DO NOT contain wildcards`() {
        val queries = listOf(
            JMdictQuery.resolve("手"),
            JMdictQuery.resolve("かわいい"),
            JMdictQuery.resolve("複雑")
        )

        queries `should equal` listOf(
            JMdictQuery.Exact("手"),
            JMdictQuery.Exact("かわいい"),
            JMdictQuery.Exact("複雑")
        )
    }
}