package com.gaumala.openjisho.utils.error

import com.gaumala.openjisho.common.UIText

class UserFriendlyException(val uiMessage: UIText,
                            val errorCode: ErrorCode,
                            cause: Exception?): Exception(cause) {

    constructor(messageResId: Int, errorCode: ErrorCode):
            this(UIText.Resource(messageResId), errorCode, null)

    constructor(messageResId: Int):
            this(UIText.Resource(messageResId), ErrorCode.general, null)
}