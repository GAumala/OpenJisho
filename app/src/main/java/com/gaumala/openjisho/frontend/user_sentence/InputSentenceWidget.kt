package com.gaumala.openjisho.frontend.user_sentence

import androidx.fragment.app.FragmentManager

class InputSentenceWidget(private val fragmentManager: FragmentManager) {
    companion object {
        private const val TAG = "inputSentenceDialog"
    }

    private fun canShowNewDialog(): Boolean {
        return fragmentManager.findFragmentByTag(TAG) == null
    }

    fun prompt(initialValue: String? = null) {
        if (!canShowNewDialog())
            return
        val f = InputSentenceDialogFragment.create(initialValue)
        f.show(fragmentManager, TAG)
    }
}