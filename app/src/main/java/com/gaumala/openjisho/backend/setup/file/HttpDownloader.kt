package com.gaumala.openjisho.backend.setup.file

import com.gaumala.openjisho.utils.ObservableInputStream
import com.gaumala.openjisho.utils.toNonNegativeLong
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

class HttpDownloader(private val url: String,
                     private val expectedSize: Long,
                     private val assertStillActive: () -> Unit,
                     private val onProgressUpdate: (Int) -> Unit) {

    private fun openConnection(outputFile: File): HttpURLConnection {
        val urlObj = URL(url)
        val connection = urlObj.openConnection() as HttpURLConnection

        if (!outputFile.exists())
            return connection

        val downloadedSize = outputFile.length()
        connection.addRequestProperty("Range", "bytes=$downloadedSize-")

        val responseCode = connection.responseCode
        if (responseCode == 416) { // Range not satisfiable
            val contentRange = connection.getHeaderField("Content-Range")
            // use a Regex to the file total length.
            // Should have a pattern like "bytes: <BYTES_SERVED>/<TOTAL_LENGTH>"
            // let's just get the numbers at the end.
            val pattern = Pattern.compile("[0-9]+$")
            val matcher = pattern.matcher(contentRange)

            if (! matcher.find())  {
                // Regex didn't work, there's probably
                // something wrong with the server
                outputFile.delete()
                return connection
            }

            val fileSizeInServer = matcher.group().toLong()
            val downloadPreviouslyCompleted = fileSizeInServer == downloadedSize
            if (downloadPreviouslyCompleted)
                throw AlreadyDownloadedException(outputFile)


            // Download is probably corrupted? try again from scratch
            outputFile.delete()
            return connection
        }

        return connection
    }

    fun download(output: File) {
        try {
            val connection = openConnection(output)
            val isResuming = connection.responseCode == HttpURLConnection.HTTP_PARTIAL
            val contentLength = connection.getHeaderField("Content-Length")
                .toNonNegativeLong(fallback = expectedSize)

            connection.inputStream.use { httpInput ->
                val input = ObservableInputStream(httpInput, assertStillActive) {
                    val progress = 100L * it / contentLength
                    onProgressUpdate(progress.toInt())
                }

                FileOutputStream(output, isResuming).use { output ->
                    input.copyTo(output)
                }
            }

        } catch (ex: AlreadyDownloadedException) {
            // Nothing to do here because the file has been already downloaded
        }
    }

    private class AlreadyDownloadedException(val cachedFile: File) : Exception()
}