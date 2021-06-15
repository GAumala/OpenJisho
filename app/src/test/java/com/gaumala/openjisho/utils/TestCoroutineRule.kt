package com.gaumala.openjisho.utils

import com.gaumala.openjisho.utils.async.CoroutineDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@ExperimentalCoroutinesApi
class TestCoroutineRule: TestRule {
    val dispatcher = TestCoroutineDispatcher()

    val testDispatchers = CoroutineDispatchers(
        dispatcher, dispatcher, dispatcher
    )

    override fun apply(statement: Statement, description: Description): Statement {
        return object : Statement() {

            override fun evaluate() {
                Dispatchers.setMain(dispatcher)

                statement.evaluate()

                Dispatchers.resetMain()
                dispatcher.cleanupTestCoroutines()

            }
        }
    }
}