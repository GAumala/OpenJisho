package com.gaumala.openjisho.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.gaumala.openjisho.R


object ClipboardUtil {
    fun copy(ctx: Context, text: String) {
        val clipboard  =
            ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val label = ctx.getString(R.string.dict_content)
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(ctx, R.string.copied_to_clipboard, Toast.LENGTH_SHORT)
            .show()
    }
}