package com.gaumala.openjisho.utils.error

import java.lang.Exception

class BetterQueriesException(val queries: List<String>): Exception() {
}