package io.twinkle.unreal.activity

import android.Manifest
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.twinkle.unreal.ui.screen.MainScreen
import io.twinkle.unreal.ui.theme.VcamProTheme
import io.twinkle.unreal.ui.viewmodel.AppsPageViewModel
import io.twinkle.unreal.ui.viewmodel.SettingsViewModel
import io.twinkle.unreal.util.ModuleStatus
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

    val settingsViewModel = SettingsViewModel()
    val appsPageViewModel = AppsPageViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val disable_file =
            File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera1/disable.jpg")
        val force_show_file =
            File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera1/force_show.jpg")
        val play_sound_file =
            File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera1/no-silent.jpg")
        val force_private_dir_file =
            File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera1/private_dir.jpg")
        val disable_toast_file =
            File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera1/no_toast.jpg")

        setContent {
            VcamProTheme(hasNavigationBar = true) {
                val cameraPermissionState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                LaunchedEffect(key1 = Unit) {
                    if (!cameraPermissionState.allPermissionsGranted) cameraPermissionState.launchMultiplePermissionRequest()
                    settingsViewModel.update {
                        it.copy(
                            forceShowPermissionErr = force_show_file.exists(),
                            temporarilyDisableModule = disable_file.exists(),
                            enableAudio = play_sound_file.exists(),
                            forcePrivateDir = force_private_dir_file.exists(),
                            disableToast = disable_toast_file.exists()
                        )
                    }
                }
                MainScreen(
                    this,
                    settingsViewModel = settingsViewModel,
                    appsPageViewModel = appsPageViewModel,
                    isActivated = ModuleStatus.isActivated()
                )
            }
        }

    }

    override fun onResume() {
        super.onResume()
        appsPageViewModel.refresh()
    }

}