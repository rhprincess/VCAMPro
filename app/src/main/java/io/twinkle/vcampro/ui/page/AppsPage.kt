package io.twinkle.vcampro.ui.page

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import io.twinkle.vcampro.R
import io.twinkle.vcampro.ui.state.AppSort
import io.twinkle.vcampro.ui.viewmodel.AppsPageViewModel
import io.twinkle.vcampro.ui.widget.AppItem
import me.zhanghai.android.appiconloader.AppIconLoader


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsPage(activity: ComponentActivity, vm: AppsPageViewModel) {
    val uiState by vm.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val iconSize = activity.resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
    val iconLoader = AppIconLoader(iconSize, false, activity)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "应用")
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
                                text = { Text("倒序") },
                                onClick = {
                                    vm.update { it.copy(reversed = !uiState.reversed) }
                                    vm.reversed()
                                },
                                trailingIcon = {
                                    Checkbox(
                                        checked = uiState.reversed,
                                        onCheckedChange = { newState -> vm.update { it.copy(reversed = newState) } })
                                })
                        }
                    }
                }
            )
        }) { innerPadding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(uiState.apps) { pi ->
                if (pi.applicationInfo.name != null) {
                    AppItem(
                        painter = BitmapPainter(
                            iconLoader.loadIcon(pi.applicationInfo).asImageBitmap()
                        ),
                        appName = pi.applicationInfo.loadLabel(activity.packageManager).toString(),
                        packageName = pi.applicationInfo.packageName
                    )
                }
            }
        }
    }
}