package io.twinkle.unreal.ui.viewmodel

import android.media.MediaMetadataRetriever
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.FileUtils
import io.twinkle.unreal.ui.state.MainUiState
import io.twinkle.unreal.ui.state.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun update(_update: (MainUiState) -> MainUiState) {
        _uiState.update(_update)
    }

    fun handleFirstFrame(path: String = uiState.value.globalVideoPath) {
        when {
            path.isEmpty() or !File(path).exists() -> {
                update {
                    it.copy(
                        globalVideoExists = false,
                        globalVideoFirstFrame = ImageBitmap(100, 100)
                    )
                }
                return
            }
        }
        val media = MediaMetadataRetriever()
        media.setDataSource(path)
        val bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        if (bitmap != null) {
            FileUtils.copy(path, "/storage/emulated/0/DCIM/Camera1/virtual.mp4")
            update {
                it.copy(globalVideoFirstFrame = bitmap.asImageBitmap(), globalVideoExists = true)
            }
        }
    }
}