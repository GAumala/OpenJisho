package com.gaumala.openjisho.utils

import java.io.IOException
import java.io.OutputStream

class ObservableOutputStream(private val wrapped: OutputStream,
                             private val assertStillActive: () -> Unit,
                             private val onBytesWritten: (Long) -> Unit): OutputStream() {
    private var bytesWritten: Long = 0

    @Throws(IOException::class)
    override fun write(b: Int) {
        assertStillActive()

        wrapped.write(b)
        bytesWritten++
        onBytesWritten(bytesWritten)
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray) {
        assertStillActive()

        wrapped.write(b)
        bytesWritten += b.size.toLong()
        onBytesWritten(bytesWritten)
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray, off: Int, len: Int) {
        assertStillActive()

        wrapped.write(b, off, len)
        bytesWritten += len.toLong()
        onBytesWritten(bytesWritten)
    }

    @Throws(IOException::class)
    override fun flush() {
        wrapped.flush()
    }

    @Throws(IOException::class)
    override fun close() {
        wrapped.close()
    }
}
