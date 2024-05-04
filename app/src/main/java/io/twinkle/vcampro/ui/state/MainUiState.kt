package io.twinkle.vcampro.ui.state

data class MainUiState(
    val forceShowPermissionErr: Boolean = false,
    val temporarilyDisableModule: Boolean = false,
    val enableAudio: Boolean = false,
    val forcePrivateDir: Boolean = false,
    val disableToast: Boolean = false
)
