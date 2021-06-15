package com.gaumala.openjisho.backend.setup.file

import android.content.res.Resources
import java.io.File


class RawResourceLoader(private val resources: Resources,
                        private val resId: Int) {

    fun loadToFile(output: File) {
        val inputStream = resources.openRawResource(resId)
        val outputStream = output.outputStream()
        inputStream.use {input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }
}