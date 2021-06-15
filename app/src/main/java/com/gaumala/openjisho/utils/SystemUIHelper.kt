package com.gaumala.openjisho.utils

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.R

class SystemUIHelper(private val activity: Activity) {

    constructor(fragment: Fragment): this(fragment.requireActivity())

    /**
     * Makes the status bar and navigation bar use colors
     * that match with the surface color of the theme.
     * For example, in a light theme, both bars would have
     * a white background
     */
    fun matchWithSurface() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        activity.window.statusBarColor =
            activity.getColorFromTheme(R.attr.colorSurface)
    }
    /**
     * Makes the status bar use primary color and navigation bar
     * use surface color.
     */
    fun matchWithPrimary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            activity.window.decorView.systemUiVisibility = 0
        }

        activity.window.statusBarColor =
            activity.getColorFromTheme(R.attr.colorPrimary)
    }
}