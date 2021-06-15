package com.gaumala.openjisho.backend.setup

import org.amshove.kluent.`should be`
import java.util.*
import kotlin.collections.HashMap

class MockedCheckpointManager: SetupCheckpointManager {
    private val checkpointMap = HashMap<Checkpoint, Boolean>()
    val marks: ArrayList<Pair<Checkpoint, Boolean>> = ArrayList()

    override fun reachedCheckpoint(checkpoint: Checkpoint): Boolean {
        return checkpointMap[checkpoint] ?: false
    }

    override fun markCheckpoint(checkpoint: Checkpoint, reached: Boolean) {
        checkpointMap[checkpoint] = reached
        marks.add(Pair(checkpoint, reached))
    }

    override fun clearAll() {
        checkpointMap.clear()
    }

    fun shouldHaveReached(checkpoint: Checkpoint) {
        reachedCheckpoint(checkpoint) `should be` true
    }
    fun shouldHaveNotReached(checkpoint: Checkpoint) {
        reachedCheckpoint(checkpoint) `should be` false
    }

}