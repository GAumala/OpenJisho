package com.gaumala.openjisho.backend

import com.gaumala.openjisho.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object VersionManager {
    const val UPDATE_URL: String = "https://gaumala.github.io/OpenJisho/"
    private const val VERSION_URL = "https://gaumala.github.io/OpenJisho/version.txt"
    private fun getLatestVersion(): String? {
        val urlObj = URL(VERSION_URL)
        try {
            val connection = urlObj.openConnection() as HttpURLConnection
            if (connection.responseCode != 200) {
                return null
            }

            return connection.inputStream.bufferedReader().use { it.readText() }
        } catch (ex: IOException) {
            return null
        }
    }

    suspend fun runIfNewVersionAvailable(callback: (String) -> Unit) {
        val latestVersion = withContext(Dispatchers.IO) {
            getLatestVersion()
        }

        if (latestVersion != null && latestVersion != BuildConfig.VERSION_NAME) {
            callback(latestVersion)
        }
    }
}