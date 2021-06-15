package com.gaumala.openjisho.backend.keyvalue

interface KeyValueStorage {
    enum class Key {
        dbSetup, // has setup been completed?
    }

    fun getBoolean(key: Key, defaultValue: Boolean): Boolean
    fun putBoolean(key: Key, value: Boolean)
}