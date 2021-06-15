package com.gaumala.openjisho.utils

import com.gaumala.mvi.Action
import java.util.*

class TestDispatcher<T, U>(initialState: T) {
    private val states = LinkedList<T>()
    private val sideEffects = LinkedList<U>()

    init {
        states.add(initialState)
    }

    fun submitAction(action: Action<T, U>) {
        val currentState = getCurrentState()

        val update = action.update(currentState)
        if (update.state != currentState)
            states.add(update.state)

        if (update.sideEffect != null)
            sideEffects.add(update.sideEffect as U)
    }

    fun getCurrentState() = states.last()
    fun getLastSideEffect() = sideEffects.lastOrNull()

    fun getAllStates(): List<T> = states
    fun getAllSideEffects(): List<U> = sideEffects
}