package com.gaumala.openjisho.backend.setup

import android.content.Context

/**
 * An object that can persist flags used during setup to be able
 * to recover more efficiently from failed attempts.
 */
interface SetupCheckpointManager {

    /**
     * This method should be called during setup before attempting something
     * to ensure that we are not doing work that was already done previously.
     */
    fun reachedCheckpoint(checkpoint: Checkpoint): Boolean

    /**
     * This method should be called during setup after completing something
     * to record that something was completed. This way, if the setup has to
     * restart, it can check for the stored flag here and skip a step.
     */
    fun markCheckpoint(checkpoint: Checkpoint, reached: Boolean)

    /**
     * This method should be called after setup completes successfully so that
     * no flags remain.
     */
    fun clearAll()

    class Default(ctx: Context): SetupCheckpointManager {

        val sharedPrefs = ctx.getSharedPreferences(
            "SetupCheckpointManager", Context.MODE_PRIVATE)

        override fun reachedCheckpoint(checkpoint: Checkpoint): Boolean {
            return sharedPrefs.getBoolean(checkpoint.toString(), false)
        }

        override fun markCheckpoint(checkpoint: Checkpoint, reached: Boolean) {
            val editor = sharedPrefs.edit()
            editor.putBoolean(checkpoint.toString(), reached).commit()
        }

        override fun clearAll() {
            Checkpoint.values().forEach { markCheckpoint(it, false) }
        }
    }
}