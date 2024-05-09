package io.twinkle.unreal.ui.state

import android.content.pm.PackageInfo
import io.twinkle.unreal.data.EnableMap

data class AppsUiState(
    val apps: List<PackageInfo> = listOf(),
    val cameraApps: List<PackageInfo> = listOf(),
    val appsShowed: List<PackageInfo> = listOf(),
    val sortBy: AppSort = AppSort.APP_NAME,
    val reversed: Boolean = false,
    val dropDownMenuExpanded: Boolean = false,
    val showCameraApps: Boolean = false,
    val showSystemApp: Boolean = false,
    val enableMap: EnableMap = EnableMap(),
    val isRefreshing: Boolean = false
)

enum class AppSort {
    APP_NAME, INSTALL_TIME, UPDATE_TIME, PACKAGE_NAME
}
