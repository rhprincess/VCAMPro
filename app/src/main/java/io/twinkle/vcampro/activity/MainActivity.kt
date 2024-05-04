package io.twinkle.vcampro.activity

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.twinkle.vcampro.ui.screen.MainScreen
import io.twinkle.vcampro.ui.theme.VcamProTheme
import io.twinkle.vcampro.ui.viewmodel.MainViewModel
import io.twinkle.vcampro.util.ModuleStatus
import java.io.File

class MainActivity : ComponentActivity() {
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
                val vm = viewModel<MainViewModel>()
                LaunchedEffect(key1 = Unit) {
                    vm.update {
                        it.copy(
                            forceShowPermissionErr = force_show_file.exists(),
                            temporarilyDisableModule = disable_file.exists(),
                            enableAudio = play_sound_file.exists(),
                            forcePrivateDir = force_private_dir_file.exists(),
                            disableToast = disable_toast_file.exists()
                        )
                    }
                }
                MainScreen(this, vm = vm, isActivated = ModuleStatus.isActivated())
            }
        }


    }

}