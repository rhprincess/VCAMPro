package io.twinkle.unreal.ui.viewmodel

import androidx.lifecycle.ViewModel
import io.twinkle.unreal.ui.state.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun update(_update: (SettingsUiState) -> SettingsUiState) {
        _uiState.update(_update)
    }
}