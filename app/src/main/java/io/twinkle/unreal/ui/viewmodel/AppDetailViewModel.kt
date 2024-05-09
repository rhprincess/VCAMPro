package io.twinkle.unreal.ui.viewmodel

import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FileUtils
import io.twinkle.unreal.data.EnableMap
import io.twinkle.unreal.ui.state.AppDetailUiState
import io.twinkle.unreal.ui.state.SettingsUiState
import io.twinkle.unreal.vcampApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

class AppDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppDetailUiState())
    val uiState: StateFlow<AppDetailUiState> = _uiState.asStateFlow()

    fun update(_update: (AppDetailUiState) -> AppDetailUiState) {
        _uiState.update(_update)
    }

    fun init(packageName: String) {
        val unreal =
            File(Environment.getExternalStorageDirectory().path + "/Android/data/" + packageName + "/files/unreal/")
        val enableFile = File(unreal.path + "/enabled")

        val jsonFile =
            File(vcampApp.filesDir.path + "/enable_list.json")
        val enableMap =
            if (jsonFile.exists()) Json.decodeFromString<EnableMap>(jsonFile.readText()) else EnableMap()

        if (!unreal.exists()) unreal.mkdirs()

        update {
            it.copy(
                videoPath = Environment.getExternalStorageDirectory().path + "/Android/data/" + packageName + "/files/unreal/virtual.mp4",
                enableUnreal = enableFile.exists(),
                enableMap = enableMap
            )
        }
        handleFirstFrame()
    }

    fun setEnableStatus(enabled: Boolean, packageName: String) {
        val enableFile =
            File(Environment.getExternalStorageDirectory().path + "/Android/data/" + packageName + "/files/unreal/enabled")
        val enableMap = uiState.value.enableMap
        enableMap.map[packageName] = enabled
        update {
            it.copy(enableUnreal = enabled, enableMap = enableMap)
        }

        viewModelScope.launch {
            val jsonFile =
                File(vcampApp.filesDir.path + "/enable_list.json")
            val jsonStr = Json.encodeToString(EnableMap.serializer(), uiState.value.enableMap)
            if (jsonFile.exists()) {
                jsonFile.writeText(jsonStr)
            } else {
                jsonFile.createNewFile()
                jsonFile.writeText(jsonStr)
            }

            if (enabled) {
                try {
                    enableFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                if (enableFile.exists()) enableFile.delete()
            }
        }
    }

    fun handleFirstFrame(path: String = uiState.value.videoPath) {
        when {
            path.isEmpty() or !File(path).exists() -> {
                update {
                    it.copy(
                        videoExits = false,
                        videoFirstFrame = ImageBitmap(100, 100)
                    )
                }
                return
            }
        }
        val media = MediaMetadataRetriever()
        media.setDataSource(path)
        val bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        if (bitmap != null) {
            FileUtils.copy(path, uiState.value.videoPath)
            update {
                it.copy(videoFirstFrame = bitmap.asImageBitmap(), videoExits = true)
            }
        }
    }

    fun deleteVideo() {
        val video = File(uiState.value.videoPath)
        video.delete().run {
            handleFirstFrame()
        }
    }
}