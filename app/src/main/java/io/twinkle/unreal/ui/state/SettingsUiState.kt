package io.twinkle.unreal.ui.state

data class SettingsUiState(
    val forceShowPermissionErr: Boolean = false,
    val temporarilyDisableModule: Boolean = false,
    val enableAudio: Boolean = false,
    val forcePrivateDir: Boolean = false,
    val disableToast: Boolean = false
)
