package com.gaumala.openjisho.backend

sealed class JMdictQuery {
    data class Exact(val queryText: String): JMdictQuery()
    data class Like(val likeText: String): JMdictQuery()
    data class EnglishMatch(val englishText: String): JMdictQuery()

    companion object {
        fun resolve(queryText: String): JMdictQuery? {

            if (queryText.isEmpty() || queryText.matches(Regex("[_% *]+")))
                return null

            if (queryText.contains(Regex("[a-zA-Z]")))
                return EnglishMatch(queryText)

            if (queryText.contains('*'))
                return Like(queryText.replace('*', '%'))

            if (queryText.contains(Regex("[_%]")))
                return Like(queryText)

            return Exact(queryText)
        }
    }
}
