package io.twinkle.vcampro.ui.page

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.alorma.compose.settings.ui.SettingsSwitch
import io.twinkle.vcampro.BuildConfig
import io.twinkle.vcampro.ui.viewmodel.MainViewModel
import io.twinkle.vcampro.util.Material3SwitchColors
import io.twinkle.vcampro.util.ModuleStatus
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(vm: MainViewModel,isActivated: Boolean = false) {
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
                    Text(text = "Virtual Camera Pro")
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }) { innerPadding ->
        // DashBoard
        Column(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        if (isActivated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(75.dp)
                        .padding(8.dp), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isActivated) Icons.Filled.Done else Icons.Filled.Close,
                        contentDescription = "isActive",
                        tint = if (isActivated) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Column {
                    Text(
                        text = if (isActivated) "已激活" else "未激活",
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "版本: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) - ${BuildConfig.BUILD_TYPE}",
                        fontSize = 13.sp
                    )
                }
            }

            Column(
                Modifier
                    .border(
                        1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                SettingsSwitch(
                    state = uiState.forceShowPermissionErr,
                    title = { Text(text = "强制显示权限缺失提示") },
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