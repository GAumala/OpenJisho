package com.gaumala.openjisho.utils

import java.io.IOException
import java.io.InputStream

class ObservableInputStream(private val wrapped: InputStream,
                            private val assertStillActive: () -> Unit,
                            private val onBytesRead: (Long) -> Unit): InputStream() {
    private var bytesRead: Long = 0

    @Throws(IOException::class)
    override fun read(): Int {
        assertStillActive()

        val res = wrapped.read()
        if (res != -1)
            bytesRead++
        onBytesRead(bytesRead)
        return res
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        assertStillActive()

        val res = wrapped.read(b)
        if (res != -1)
            bytesRead += res
        onBytesRead(bytesRead)
        return res
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        assertStillActive()

        val res = wrapped.read(b, off, len)
        if (res != -1)
            bytesRead += res
        onBytesRead(bytesRead)
        return res
    }

    @Throws(IOException::class)
    override fun close() {
        wrapped.close()
    }
}