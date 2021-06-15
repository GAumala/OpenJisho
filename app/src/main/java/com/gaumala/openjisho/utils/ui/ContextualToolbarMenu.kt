package com.gaumala.openjisho.utils.ui

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode

/** Abstraction for objects that can show a menu in the toolbar
 * It only exposes methods for toggling menu visibility and
 * setting listeners for item click and menu close events.
 *
 * The listeners should be set before calling show() or hide().
 *
 */
interface ContextualToolbarMenu {
    fun setItemClickedListener(listener: (Int) -> Unit)
    fun setOnCloseListener(listener: () -> Unit)
    fun show()
    fun hide()

    class Default(private val activity: AppCompatActivity,
                  private val menuResId: Int):
        ContextualToolbarMenu {
        private var actionMode: ActionMode? = null
        private lateinit var onItemClickListener: (Int) -> Unit
        private lateinit var onCloseListener: () -> Unit

        private val modeCallback = object: ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
                onItemClickListener(item.itemId)
                return true
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(menuResId, menu)
                return true

            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                onCloseListener()
                actionMode = null
            }
        }

        private val onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onCloseListener()
            }
        }

        override fun setItemClickedListener(listener: (Int) -> Unit) {
            onItemClickListener = listener
        }

        override fun setOnCloseListener(listener: () -> Unit) {
            onCloseListener = listener
        }

        override fun show() {
            if (actionMode != null)
                return

            activity.onBackPressedDispatcher
                .addCallback(onBackPressedCallback)
            actionMode = activity.startSupportActionMode(modeCallback)
        }

        override fun hide() {
            actionMode?.finish() ?: return
            onBackPressedCallback.remove()
        }

    }
}