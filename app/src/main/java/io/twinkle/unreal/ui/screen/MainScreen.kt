package io.twinkle.unreal.ui.screen

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import io.twinkle.unreal.R
import io.twinkle.unreal.data.EnableMap
import io.twinkle.unreal.ui.page.AppsPage
import io.twinkle.unreal.ui.page.HomePage
import io.twinkle.unreal.ui.page.SettingsPage
import io.twinkle.unreal.ui.state.AppSort
import io.twinkle.unreal.ui.viewmodel.AppsPageViewModel
import io.twinkle.unreal.ui.viewmodel.MainViewModel
import io.twinkle.unreal.ui.viewmodel.SettingsViewModel
import io.twinkle.unreal.util.Settings
import io.twinkle.unreal.util.settings
import io.twinkle.unreal.vcampApp
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    activity: ComponentActivity,
    settingsViewModel: SettingsViewModel = viewModel(),
    appsPageViewModel: AppsPageViewModel = viewModel(),
    isActivated: Boolean = false
) {
    val pagerState = rememberPagerState(initialPage = 1) { 3 }
    val items = listOf("应用", "主页", "设置")
    val mainViewModel = viewModel<MainViewModel>()

    LaunchedEffect(key1 = Unit) {
        Runnable { appsPageViewModel.refresh() }.run()
        mainViewModel.update { it.copy(globalVideoExists = File(it.globalVideoPath).exists()) }
        mainViewModel.handleFirstFrame()
    }

    Column(Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> AppsPage(activity = activity, vm = appsPageViewModel)
                1 -> HomePage(vm = mainViewModel, isActivated)
                2 -> SettingsPage(vm = settingsViewModel)
            }
        }

        NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    label = { Text(text = item) },
                    selected = index == pagerState.currentPage,
                    onClick = {
                        mainViewModel.viewModelScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                    alwaysShowLabel = false,
                    icon = {
                        val icon = when (index) {
                            0 -> ImageVector.vectorResource(R.drawable.apps)
                            1 -> Icons.Outlined.Home
                            2 -> Icons.Outlined.Settings
                            else -> return@NavigationBarItem
                        }
                        Icon(imageVector = icon, contentDescription = "navigation icon")
                    })
            }
        }
    }

}