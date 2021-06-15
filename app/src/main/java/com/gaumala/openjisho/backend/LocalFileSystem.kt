package com.gaumala.openjisho.backend

import android.content.Context
import com.gaumala.openjisho.utils.AsyncFileHandler
import kotlinx.coroutines.CoroutineScope
import java.io.File

interface LocalFileSystem {
    fun getListsDirectory(): File
    fun getListFile(filename: String): AsyncFileHandler

    class Default(ctx: Context,
                  private val scope: CoroutineScope): LocalFileSystem {
        private val listsDir = File(ctx.cacheDir, "lists")
        init {
            listsDir.mkdir()
        }

        override fun getListsDirectory(): File = listsDir

        override fun getListFile(filename: String): AsyncFileHandler {
            val file = File(listsDir, filename)
            return AsyncFileHandler.ChannelFileHandler(scope, file.absolutePath)
        }

    }
}