package io.twinkle.unreal.ui.page

import android.os.Environment
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsSwitch
import io.twinkle.unreal.BuildConfig
import io.twinkle.unreal.R
import io.twinkle.unreal.ui.viewmodel.SettingsViewModel
import io.twinkle.unreal.util.Material3SwitchColors
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(vm: SettingsViewModel) {
    val uiState by vm.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
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
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(text = "设置")
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) - ${BuildConfig.BUILD_TYPE}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                SettingsGroup(title = {
                    Text(
                        text = "模块",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }) {
                    SettingsSwitch(
                        state = uiState.forceShowPermissionErr,
                        title = { Text(text = "强制显示权限缺失提示") },
                        icon = {
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.force_permission_lack),
                                    contentDescription = "lack"
                                )
                            }
                        },
                        switchColors = Material3SwitchColors()
                    ) { newState ->
                        vm.viewModelScope.launch {
                            if (force_show_file.exists() && !newState) {
                                force_show_file.delete()
                            } else {
                                force_show_file.createNewFile()
                            }
                        }
                        vm.update { it.copy(forceShowPermissionErr = newState) }
                    }

                    SettingsSwitch(
                        state = uiState.temporarilyDisableModule,
                        title = { Text(text = "临时关闭模块") },
                        subtitle = {
                            Text(
                                text = "当模块在运行过程中出现技术性故障或Bug的时候，临时关闭模块的所有Hook操作",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        },
                        icon = {
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.outline_play_disabled_24),
                                    contentDescription = "disable module"
                                )
                            }
                        },
                        switchColors = Material3SwitchColors()
                    ) { newState ->
                        vm.viewModelScope.launch {
                            if (disable_file.exists() && !newState) {
                                disable_file.delete()
                            } else {
                                disable_file.createNewFile()
                            }
                        }
                        vm.update { it.copy(temporarilyDisableModule = newState) }
                    }

                    SettingsSwitch(
                        state = uiState.enableAudio,
                        title = { Text(text = "播放视频声音") },
                        subtitle = {
                            Text(
                                text = "开启后将在相机预览界面播放视频声音，同时录制的视频也会带有声音",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        },
                        icon = {
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.outline_audiotrack_24),
                                    contentDescription = "audio"
                                )
                            }
                        },
                        switchColors = Material3SwitchColors()
                    ) { newState ->
                        vm.viewModelScope.launch {
                            if (play_sound_file.exists() && newState) {
                                play_sound_file.delete()
                            } else {
                                play_sound_file.createNewFile()
                            }
                        }
                        vm.update { it.copy(enableAudio = newState) }
                    }

                    SettingsSwitch(
                        state = uiState.forcePrivateDir,
                        title = { Text(text = "强制每个应用程序使用私有目录") },
                        icon = {
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.outline_insert_drive_file_24),
                                    contentDescription = "internal storage"
                                )
                            }
                        },
                        switchColors = Material3SwitchColors()
                    ) { newState ->
                        vm.viewModelScope.launch {
                            if (force_private_dir_file.exists() && !newState) {
                                force_private_dir_file.delete()
                            } else {
                                force_private_dir_file.createNewFile()
                            }
                        }
                        vm.update { it.copy(forcePrivateDir = newState) }
                    }

                    SettingsSwitch(
                        state = uiState.disableToast,
                        title = { Text(text = "关闭提示消息") },
                        icon = {
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.outline_notification_important_24),
                                    contentDescription = "disable toast"
                                )
                            }
                        },
                        switchColors = Material3SwitchColors()
                    ) { newState ->
                        vm.viewModelScope.launch {
                            if (disable_toast_file.exists() && !newState) {
                                disable_toast_file.delete()
                            } else {
                                disable_toast_file.createNewFile()
                            }
                        }
                        vm.update { it.copy(disableToast = newState) }
                    }
                }

            }
        }
    }
}