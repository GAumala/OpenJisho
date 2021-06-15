package com.gaumala.openjisho.utils

import com.gaumala.openjisho.common.JMdictEntry

fun createTags(vararg codes: String) = codes.map {
    JMdictEntry.Tag.parse(it)
}

