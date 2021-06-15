package com.gaumala.openjisho.frontend.dict

import android.app.Dialog
import android.os.Bundle
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.navigation.MainScreen
import com.gaumala.openjisho.frontend.navigation.Navigator


class RebuildDialog private constructor(
    private val ctx: Context,
    private val listener: OnClosedListener) {


    private fun createDialog(): AlertDialog {
        return AlertDialog.Builder(ctx)
            .setTitle(R.string.rebuild_database)
            .setMessage(R.string.are_you_sure_rebuild)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                listener.onAccepted()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    class ContainerFragment : DialogFragment() {

        private fun rebuildDatabase() {
            val activity = activity as Navigator


            activity.goTo(MainScreen.Setup)
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = RebuildDialog(activity!!, object : OnClosedListener {
                override fun onAccepted() {
                    rebuildDatabase()
                }
            })

            return dialog.createDialog()
        }
    }

    class Manager(private val fragmentManager: FragmentManager) {

        private fun canShowNewDialog(): Boolean {
            return fragmentManager.findFragmentByTag(TAG) == null
        }

        fun prompt() {
            if (! canShowNewDialog())
                return
            val f = newInstance()
            f.show(fragmentManager, TAG)
        }
    }

    interface OnClosedListener {
        fun onAccepted()
    }

    companion object {
        private val TAG = "RebuildDialog"
        fun newInstance(): ContainerFragment {
            return ContainerFragment()
        }
    }
}