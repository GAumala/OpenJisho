package com.gaumala.openjisho.frontend.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gaumala.mvi.Dispatcher
import com.gaumala.mvi.DispatcherViewModel
import com.gaumala.mvi.SideEffectRunner
import com.gaumala.openjisho.common.SetupStep

class SetupViewModel : DispatcherViewModel<SetupState, Void>() {
    class Factory(): ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val initialState = SetupState.Working(SetupStep.initializing, -1)
            val newDispatcher = Dispatcher(
                SideEffectRunner<SetupState, Void> { _, _ ->}, initialState)

            val viewModel = SetupViewModel()
            viewModel.setDispatcher(newDispatcher)

            return viewModel as T
        }
    }
}
