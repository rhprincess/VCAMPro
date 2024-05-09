package io.twinkle.unreal.ui.state

import androidx.compose.ui.graphics.ImageBitmap

data class MainUiState(
    val globalVideoExists: Boolean = false,
    val globalVideoPath: String = "/storage/emulated/0/DCIM/Camera1/virtual.mp4",
    val globalVideoFirstFrame: ImageBitmap = ImageBitmap(100, 100)
)
