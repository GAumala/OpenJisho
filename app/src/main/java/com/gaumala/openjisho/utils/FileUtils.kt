package com.gaumala.openjisho.utils

import java.io.File
import java.io.BufferedReader
import android.os.StatFs
import android.os.Environment.getDataDirectory


fun File.countLines(): Int {
    val reader = BufferedReader(this.reader())
    var lines = 0
    while (reader.readLine() != null) lines++
    reader.close()
    return lines
}

object FileUtils {
    fun getAvailableSpace(): Long {
        val path = getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return availableBlocks * blockSize
    }
}


