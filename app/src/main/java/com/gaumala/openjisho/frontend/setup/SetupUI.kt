package com.gaumala.openjisho.frontend.setup

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.UIText
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.BaseUI
import com.gaumala.openjisho.frontend.navigation.MainScreen
import com.gaumala.openjisho.frontend.navigation.Navigator
import com.gaumala.openjisho.frontend.setup.actions.CompleteSetup
import com.gaumala.openjisho.frontend.setup.actions.RestartSetup
import com.gaumala.openjisho.frontend.setup.actions.UpdateProgress
import com.gaumala.openjisho.utils.image.MatrixCalculator
import com.gaumala.openjisho.utils.image.MatrixImageView
import com.gaumala.openjisho.utils.setProgressCompat

class SetupUI(owner: LifecycleOwner,
              view: View,
              private val navigator: Navigator,
              private val restartService: () -> Unit,
              private val sink: ActionSink<SetupState, Void>,
              liveState: LiveData<SetupState>): BaseUI<SetupState>(owner, liveState) {

    private val ctx = view.context
    private val horizontalProgress: ProgressBar = view.findViewById(R.id.progress_horizontal)
    private val loadingArt: MatrixImageView = view.findViewById(R.id.loading_art)
    private val progressGroup: Group = view.findViewById(R.id.progress_views)
    private val errorGroup: Group = view.findViewById(R.id.error_views)
    private val progressCaption: TextView = view.findViewById(R.id.progress_caption)
    private val errorTextView: TextView = view.findViewById(R.id.error_text)
    private val retryButton: View = view.findViewById(R.id.retry_button)

    init {
        loadingArt.matrixCalculator = MatrixCalculator.FitBottom()
    }

    override fun rebind(state: SetupState) {
        when (state) {
            is SetupState.Working -> setupWorkingState(state)
            is SetupState.Error -> setupErrorState(state)
            is SetupState.Done -> setupDoneState()
        }
    }

    private fun setupWorkingState(state: SetupState.Working) {
        progressGroup.visibility = View.VISIBLE

        val captionText = state.step.toString(ctx)
        progressCaption.text = captionText

        if (state.progress < 0)
            horizontalProgress.isIndeterminate = true
        else {
            horizontalProgress.isIndeterminate = false
            val percentage = state.progress
            horizontalProgress.setProgressCompat(percentage, true)
        }

        errorGroup.visibility = View.GONE
        retryButton.setOnClickListener(null)
    }

    private fun setupErrorState(errorState: SetupState.Error) {
        progressGroup.visibility = View.INVISIBLE
        errorGroup.visibility = View.VISIBLE

        errorTextView.text = errorState.text.getText(ctx)
        retryButton.setOnClickListener {
            restartService()
            sink.submitAction(RestartSetup())
        }
    }

    private fun setupDoneState() {
        navigator.goTo(MainScreen.Dictionary())
    }

    fun updateProgress(step: SetupStep, progress: Int) =
        sink.submitAction(UpdateProgress(step, progress))

    fun completeSetup(errorText: UIText?) =
        sink.submitAction(CompleteSetup(errorText))

    fun startAnimation() {
        val drawable = loadingArt.drawable as AnimationDrawable
        drawable.start()
    }

    fun stopAnimation() {
        val drawable = loadingArt.drawable as AnimationDrawable
        drawable.stop()
    }

}