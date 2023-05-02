package com.gaumala.openjisho.backend

sealed class TatoebaQuery {
    data class Japanese(val matchText: String): TatoebaQuery()
    data class English(val matchText: String): TatoebaQuery()

    companion object {
        fun resolve(queryText: String): TatoebaQuery? {
            val sanitizedText =
                queryText.replace(Regex("[_%*]+"), "")

            if (sanitizedText.isEmpty()) return null

            if (sanitizedText.contains(Regex("[a-zA-Z]")))
                return English(sanitizedText)

            return Japanese(sanitizedText)
        }
    }
}