package com.gaumala.openjisho.frontend.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.R
import com.gaumala.openjisho.backend.setup.SetupService
import com.gaumala.openjisho.frontend.navigation.MainScreen
import com.gaumala.openjisho.frontend.navigation.Navigator
import com.gaumala.openjisho.utils.SystemUIHelper

/**
 * A fragment displayed when the user opens the app for the first time.
 * It greets the user, informs about the first time setup and displays
 * a button that the user can click to start it.
 */
class WelcomeFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        val view = inflater.inflate(
            R.layout.welcome_fragment, container, false)
        bindView(view)

        SystemUIHelper(this).matchWithSurface()
        return view
    }

    private fun startDownload() {
        requireActivity().apply {
            val intent = Intent(this, SetupService::class.java)
            startService(intent)
        }
    }

    private fun showTour() {
        val navigator = activity as Navigator
        navigator.goTo(MainScreen.Tour(true))
    }

    private fun bindView(view: View) {
        val startButton = view.findViewById<View>(R.id.start_btn)
        startButton.setOnClickListener {
            startDownload()
            showTour()
        }
    }

    companion object {
        fun newInstance(): WelcomeFragment {
            return WelcomeFragment()
        }
    }
}