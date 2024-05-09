package io.twinkle.unreal.ui.screen

import android.Manifest
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Trace
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsSwitch
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PathUtils
import io.twinkle.unreal.R
import io.twinkle.unreal.activity.AppDetailActivity
import io.twinkle.unreal.data.EnableMap
import io.twinkle.unreal.ui.theme.VcamProTheme
import io.twinkle.unreal.ui.viewmodel.AppDetailViewModel
import io.twinkle.unreal.ui.widget.AppItem
import io.twinkle.unreal.ui.widget.WarningBar
import io.twinkle.unreal.util.Material3SwitchColors
import io.twinkle.unreal.util.toAbsoluteFilePath
import io.twinkle.unreal.vcampApp
import kotlinx.serialization.json.Json
import me.zhanghai.android.appiconloader.AppIconLoader
import java.io.File
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    activity: AppDetailActivity,
    packageInfo: PackageInfo,
    vm: AppDetailViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val iconSize = activity.resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
    val iconLoader = AppIconLoader(iconSize, false, activity)
    val isCameraApp =
        if (packageInfo.requestedPermissions != null) packageInfo.requestedPermissions.contains(
            android.Manifest.permission.CAMERA
        ) else false
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }

    var hasPermission by rememberSaveable { mutableStateOf(false) }

    val openSelectVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {//这里返回的是uri？
            it?.let { uri ->
                val filePath = uri.toAbsoluteFilePath(context)
                vm.handleFirstFrame(filePath)
            }
        })

    LaunchedEffect(key1 = Unit) {
        hasPermission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                activity.packageManager.checkPermission(
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    packageInfo.applicationInfo.packageName
                ) == PackageManager.PERMISSION_GRANTED
            }

            else -> {
                activity.packageManager.checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    packageInfo.applicationInfo.packageName
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "应用配置") },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
            navigationIcon = {
                IconButton(onClick = { activity.finish() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            WarningBar(
                visible = !hasPermission,
                text = { Text(text = "该应用尚未获取读取外部存储或访问外部存储的权限，设置可能无法生效，点击按钮打开应用详情页授予权限") },
                actions = {
                    TextButton(onClick = { AppUtils.launchAppDetailsSettings(packageInfo.applicationInfo.packageName) }) {
                        Text(text = "打开")
                    }
                }
            )

            SettingsGroup(title = {
                Text(
                    text = "应用",
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }) {
                Column(
                    Modifier
                        .clickable {
                            AppUtils.launchAppDetailsSettings(packageInfo.applicationInfo.packageName)
                        }
                        .fillMaxWidth()
                ) {
                    AppItem(
                        painter = BitmapPainter(
                            iconLoader.loadIcon(packageInfo.applicationInfo).asImageBitmap()
                        ),
                        appName = packageInfo.applicationInfo.loadLabel(activity.packageManager)
                            .toString(),
                        packageName = packageInfo.applicationInfo.packageName,
                        isCameraApp = isCameraApp,
                        isEnable = false
                    )
                    Text(
                        text = "外置目录: " + Environment.getExternalStorageDirectory().path + "/Android/data/" + packageInfo.applicationInfo.packageName + "/files/unreal/",
                        modifier = Modifier.padding(start = 88.dp, end = 16.dp),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                SettingsSwitch(
                    state = uiState.enableUnreal,
                    title = { Text(text = "启用") },
                    icon = {
                        Box(
                            modifier = Modifier.size(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_blur_on_24),
                                contentDescription = "enable"
                            )
                        }
                    },
                    switchColors = Material3SwitchColors()
                ) { newState ->
                    vm.setEnableStatus(
                        enabled = newState,
                        packageName = packageInfo.applicationInfo.packageName
                    )
                }
            }

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = uiState.enableUnreal,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            if (isCameraApp) {
                                openSelectVideoLauncher.launch("video/*")
                            } else {
                                showDialog = true
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        bitmap = uiState.videoFirstFrame,
                        contentDescription = "video first frame",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f),
                                        Color.Transparent
                                    ),
                                    tileMode = TileMode.Decal
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Text(text = "选择视频", modifier = Modifier.padding(start = 32.dp))
                    if (uiState.videoExits) {
                        IconButton(
                            modifier = Modifier
                                .padding(end = 32.dp)
                                .align(Alignment.CenterEnd),
                            onClick = {
                                vm.deleteVideo()
                            }) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "delete")
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    title = { Text(text = "非相机应用") },
                    text = { Text(text = "此应用非相机应用，选择虚拟视频可能对该应用没有任何效果，您确定要继续吗？") },
                    onDismissRequest = { showDialog = false },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(text = "取消")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            openSelectVideoLauncher.launch("video/*")
                            showDialog = false
                        }) {
                            Text(text = "确定")
                        }
                    }
                )
            }
        }
    }
}