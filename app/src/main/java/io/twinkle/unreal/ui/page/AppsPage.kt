package io.twinkle.unreal.ui.page

import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import io.twinkle.unreal.R
import io.twinkle.unreal.activity.AppDetailActivity
import io.twinkle.unreal.ui.state.AppSort
import io.twinkle.unreal.ui.viewmodel.AppsPageViewModel
import io.twinkle.unreal.ui.widget.AppItem
import io.twinkle.unreal.util.Settings
import io.twinkle.unreal.util.settings
import io.twinkle.unreal.vcampApp
import kotlinx.coroutines.launch
import me.zhanghai.android.appiconloader.AppIconLoader
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppsPage(activity: ComponentActivity, vm: AppsPageViewModel) {
    val uiState by vm.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val iconSize = activity.resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
    val iconLoader = AppIconLoader(iconSize, false, activity)
    val appList = if (uiState.showSystemApp) uiState.appsShowed else uiState.appsShowed.filter {
        (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "应用 (${appList.size})")
                }, scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    Box {
                        IconButton(onClick = { vm.showDropDownMenu() }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.sort),
                                contentDescription = "Sort"
                            )
                        }
                        DropdownMenu(
                            expanded = uiState.dropDownMenuExpanded,
                            offset = DpOffset(
                                0.dp, (-48).dp
                            ),
                            onDismissRequest = { vm.closeDropDownMenu() }
                        ) {
                            DropdownMenuItem(
                                text = { Text("应用名称") },
                                onClick = {
                                    vm.sortBy(AppSort.APP_NAME)
                                },
                                trailingIcon = {
                                    RadioButton(
                                        selected = uiState.sortBy == AppSort.APP_NAME,
                                        onClick = { vm.sortBy(AppSort.APP_NAME) })
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("安装时间") },
                                onClick = {
                                    vm.sortBy(AppSort.INSTALL_TIME)
                                },
                                trailingIcon = {
                                    RadioButton(
                                        selected = uiState.sortBy == AppSort.INSTALL_TIME,
                                        onClick = { vm.sortBy(AppSort.INSTALL_TIME) })
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("更新时间") },
                                onClick = {
                                    vm.sortBy(AppSort.UPDATE_TIME)
                                },
                                trailingIcon = {
                                    RadioButton(
                                        selected = uiState.sortBy == AppSort.UPDATE_TIME,
                                        onClick = { vm.sortBy(AppSort.UPDATE_TIME) })
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("应用包名") },
                                onClick = {
                                    vm.sortBy(AppSort.PACKAGE_NAME)
                                },
                                trailingIcon = {
                                    RadioButton(
                                        selected = uiState.sortBy == AppSort.PACKAGE_NAME,
                                        onClick = { vm.sortBy(AppSort.PACKAGE_NAME) })
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("相机应用") },
                                onClick = {
                                    val newState = !uiState.showCameraApps
                                    vm.update {
                                        it.copy(
                                            showCameraApps = newState
                                        )
                                    }
                                    vm.viewModelScope.launch {
                                        vcampApp.settings.edit {
                                            it[Settings.SHOW_CAMERA_APPS] = newState
                                        }
                                    }
                                    vm.sortBy(uiState.sortBy)
                                },
                                trailingIcon = {
                                    Checkbox(
                                        checked = uiState.showCameraApps,
                                        onCheckedChange = { newState ->
                                            vm.update {
                                                it.copy(
                                                    showCameraApps = newState
                                                )
                                            }
                                            vm.viewModelScope.launch {
                                                vcampApp.settings.edit {
                                                    it[Settings.SHOW_CAMERA_APPS] = newState
                                                }
                                            }
                                            vm.sortBy(uiState.sortBy)
                                        })
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("系统应用") },
                                onClick = {
                                    val newState = !uiState.showSystemApp
                                    vm.update {
                                        it.copy(showSystemApp = newState)
                                    }
                                    vm.viewModelScope.launch {
                                        vcampApp.settings.edit {
                                            it[Settings.SHOW_SYSTEM_APPS] = newState
                                        }
                                    }
                                },
                                trailingIcon = {
                                    Checkbox(
                                        checked = uiState.showSystemApp,
                                        onCheckedChange = { newState ->
                                            vm.update {
                                                it.copy(showSystemApp = newState)
                                            }
                                            vm.viewModelScope.launch {
                                                vcampApp.settings.edit {
                                                    it[Settings.SHOW_SYSTEM_APPS] = newState
                                                }
                                            }
                                        }
                                    )
                                })
                            DropdownMenuItem(
                                text = { Text("倒序") },
                                onClick = {
                                    vm.update { it.copy(reversed = !uiState.reversed) }
                                    vm.reversed()
                                },
                                trailingIcon = {
                                    Checkbox(
                                        checked = uiState.reversed,
                                        onCheckedChange = { newState ->
                                            vm.update { it.copy(reversed = newState) }
                                            vm.reversed()
                                        }
                                    )
                                })
                        }
                    }
                }
            )
        }) { innerPadding ->
        val listState = rememberLazyListState()
        LazyColumnScrollbar(
            state = listState,
            settings = ScrollbarSettings(
                thumbUnselectedColor = MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.75f
                ), thumbSelectedColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            indicatorContent = { index, isThumbSelected ->
                Box(modifier = Modifier.padding(8.dp)) {
                    Box(
                        Modifier
                            .size(42.dp)
                            .background(
                                shape = CircleShape,
                                color = if (isThumbSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (appList.isNotEmpty() && index <= appList.size) appList[index].applicationInfo.loadLabel(
                                activity.packageManager
                            )
                                .first().toString() else "",
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (isThumbSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        ) {
            LazyColumn(state = listState) {
                items(appList) { pi ->
                    if (pi.applicationInfo.name != null) {
                        val isCameraApp =
                            if (pi.requestedPermissions != null) pi.requestedPermissions.contains(
                                android.Manifest.permission.CAMERA
                            ) else false

                        AppItem(
                            modifier = Modifier
                                .animateItem()
                                .clickable {
                                    val intent = Intent()
                                    intent.setClass(activity, AppDetailActivity::class.java)
                                    intent.putExtra(
                                        "package_name",
                                        pi.applicationInfo.packageName
                                    )
                                    activity.startActivity(intent)
                                },
                            painter = BitmapPainter(
                                iconLoader.loadIcon(pi.applicationInfo).asImageBitmap()
                            ),
                            appName = pi.applicationInfo.loadLabel(activity.packageManager)
                                .toString(),
                            packageName = pi.applicationInfo.packageName,
                            isCameraApp = isCameraApp,
                            isEnable = uiState.enableMap.map[pi.applicationInfo.packageName]
                                ?: false
                        )
                    }
                }
            }
        }

    }
}