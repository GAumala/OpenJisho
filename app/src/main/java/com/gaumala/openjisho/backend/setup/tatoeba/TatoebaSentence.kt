package com.gaumala.openjisho.backend.setup.tatoeba


data class TatoebaSentence(val id: Long, val lang: Lang, val sentence: String) {
    enum class Lang {
        eng, jpn, unknown;

        companion object {
            fun fromCode(code: String) =
                    when (code) {
                        "eng" -> eng
                        "jpn" -> jpn
                        else -> unknown
                    }
        }
    }
}