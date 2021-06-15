package com.gaumala.openjisho.frontend.setup

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gaumala.openjisho.R
import com.gaumala.openjisho.backend.setup.SetupService
import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import android.content.ServiceConnection
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.backend.setup.SetupServiceListener
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.frontend.navigation.Navigator
import com.gaumala.openjisho.utils.SystemUIHelper

/**
 * The fragment displayed during the first time setup. It manages the
 * [SetupService] and displays setup progress to the user. Once setup
 * completes it automatically navigates to
 * [com.gaumala.openjisho.frontend.dict.DictFragment].
 */
class SetupFragment : Fragment() {

    companion object {
        fun newInstance() = SetupFragment()
    }

    private lateinit var ui: SetupUI
    var serviceFrontend: SetupService.Frontend? = null

    private val runService: () -> Unit = {
        val act = activity!!

        val intent = Intent(act, SetupService::class.java)
        act.startService(intent)
        act.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val setupServiceListener = object :SetupServiceListener {
        override fun onProgressChange(step: SetupStep, progress: Int) {
            ui.updateProgress(step, progress)
        }

        override fun onComplete(errorText: UIText?) {
            ui.completeSetup(errorText)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder) {
            val frontend = service as SetupService.Frontend
            serviceFrontend = frontend

            frontend.bind(setupServiceListener)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceFrontend = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val viewModel = ViewModelProviders
            .of(this, SetupViewModel.Factory())
            .get(SetupViewModel::class.java)

        val view = inflater.inflate(R.layout.setup_fragment,
            container, false)
        ui = SetupUI(
            owner = this,
            navigator = activity as Navigator,
            view = view!!,
            sink = viewModel.userActionSink,
            restartService = runService,
            liveState = viewModel.liveState)
        ui.subscribe()

        SystemUIHelper(this).matchWithSurface()

        runService()
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unbindService(serviceConnection)
    }

    override fun onStart() {
        super.onStart()
        // Everytime the fragment restarts we must manually check the status
        // of the service to check if the setup completed while the fragment
        // was stopped.
        serviceFrontend?.bind(setupServiceListener)
        ui.startAnimation()
    }

    override fun onStop() {
        super.onStop()
        serviceFrontend?.unbind()
        ui.stopAnimation()
    }
}
