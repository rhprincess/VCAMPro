package io.twinkle.unreal.ui.state

import androidx.compose.ui.graphics.ImageBitmap
import io.twinkle.unreal.data.EnableMap

data class AppDetailUiState(
    val enableUnreal: Boolean = false,
    val videoExits: Boolean = false,
    val videoPath: String = "",
    val videoFirstFrame: ImageBitmap = ImageBitmap(100, 100),
    val enableMap: EnableMap = EnableMap()
)
