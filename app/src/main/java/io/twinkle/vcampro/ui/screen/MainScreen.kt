package io.twinkle.vcampro.ui.screen

import android.Manifest
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alorma.compose.settings.ui.SettingsSwitch
import com.highcapable.yukihookapi.YukiHookAPI
import io.twinkle.vcampro.BuildConfig
import io.twinkle.vcampro.R
import io.twinkle.vcampro.ui.page.AppsPage
import io.twinkle.vcampro.ui.page.HomePage
import io.twinkle.vcampro.ui.page.SettingsPage
import io.twinkle.vcampro.ui.state.AppSort
import io.twinkle.vcampro.ui.viewmodel.AppsPageViewModel
import io.twinkle.vcampro.ui.viewmodel.MainViewModel
import io.twinkle.vcampro.util.Material3SwitchColors
import io.twinkle.vcampro.util.ModuleStatus
import io.twinkle.vcampro.util.Settings
import io.twinkle.vcampro.util.settings
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    activity: ComponentActivity,
    vm: MainViewModel = viewModel(),
    isActivated: Boolean = false
) {
    val pagerState = rememberPagerState(initialPage = 1) { 3 }
    val items = listOf("应用", "主页", "设置")
    val appsPageViewModel = viewModel<AppsPageViewModel>()

    LaunchedEffect(key1 = Unit) {
        appsPageViewModel.update { it.copy(apps = activity.packageManager.getInstalledPackages(0)) }
        appsPageViewModel.sortBy(AppSort.UPDATE_TIME)
    }

    Column(Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> AppsPage(activity = activity, vm = appsPageViewModel)
                1 -> HomePage(vm = vm, isActivated)
                2 -> SettingsPage()
            }
        }

        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    label = { Text(text = item) },
                    selected = index == pagerState.currentPage,
                    onClick = {
                        vm.viewModelScope.launch {
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