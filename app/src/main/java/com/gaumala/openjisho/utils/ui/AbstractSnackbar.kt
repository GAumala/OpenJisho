package com.gaumala.openjisho.utils.ui

import android.view.ViewGroup
import com.gaumala.openjisho.common.UIText
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

/**
 * Interface for objects that can show a snack bar with
 * a message, and optionally an action like undo.
 */
interface AbstractSnackbar {
    /**
     * Shows a message with a snack bar.
     * @param message the message to display in the snack bar. If it
     * is UIText.empty it should do nothing. If the message is already
     * currently visible because of a recent invocation, it should do
     * nothing.
     * @param action the text of the action to add to the snack bar
     * if it is UIText.empty then the snack bar is displayed without
     * action
     * @param onDismissed callback executed when the snack bar is
     * dismissed. it is called with true only if it was dismissed after
     * the user clicked the on action.
     */
    fun show(message: UIText,
             action: UIText = UIText.empty,
             onDismissed: (Boolean) -> Unit)

    class Default(private val container: ViewGroup):
        AbstractSnackbar {
        private val ctx = container.context
        private var lastMessage: String? = null

        private fun createCallback(lambda: (Boolean) -> Unit) = object: Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                when (event) {
                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION ->
                        lambda(true)
                    else -> lambda(false)
                }

                lastMessage = null
            }

        }

        override fun show(message: UIText,
                          action: UIText,
                          onDismissed: (Boolean) -> Unit) {

            if (message == UIText.empty)
                return

            val messageText = message.getText(ctx)
            if (messageText == lastMessage)
                return
            lastMessage = messageText

            val snackbar = Snackbar.make(
                container,
                message.getText(ctx),
                Snackbar.LENGTH_LONG)
            snackbar.addCallback(createCallback(onDismissed))

            if (action != UIText.empty)
                snackbar.setAction(action.getText(ctx)) {}

            snackbar.show()
        }

    }
}