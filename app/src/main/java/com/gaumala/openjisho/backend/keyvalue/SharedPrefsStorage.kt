package com.gaumala.openjisho.backend.keyvalue

import android.content.Context


class SharedPrefsStorage(ctx: Context): KeyValueStorage {
    val sharedPrefs = ctx.getSharedPreferences(
        "JDictPrefs", Context.MODE_PRIVATE)

    override fun getBoolean(key: KeyValueStorage.Key,
                            defaultValue: Boolean): Boolean {
        return sharedPrefs.getBoolean(key.toString(), defaultValue)
    }

    override fun putBoolean(key: KeyValueStorage.Key, value: Boolean) {
        val editor = sharedPrefs.edit()
        editor.putBoolean(key.toString(), value)
        editor.apply()
    }
}