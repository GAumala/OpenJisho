package com.gaumala.openjisho.frontend.dict

import com.gaumala.mvi.ActionSink

class DictSearchMsg(val params: DictSearchParams,
                    val sink: ActionSink<DictState, DictSideEffect>) {

    override fun equals(other: Any?): Boolean {
        val otherMsg = other as? DictSearchMsg ?: return false
        return params == otherMsg.params
    }

    override fun toString(): String {
        return "DictSearchMsg{params=$params}"
    }

    override fun hashCode(): Int {
        return params.hashCode()
    }
}