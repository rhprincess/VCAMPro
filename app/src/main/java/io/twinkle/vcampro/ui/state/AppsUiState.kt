package io.twinkle.vcampro.ui.state

import android.content.pm.PackageInfo

data class AppsUiState(
    val apps: List<PackageInfo> = listOf(),
    val sortBy: AppSort = AppSort.APP_NAME,
    val reversed: Boolean = false,
    val dropDownMenuExpanded: Boolean = false
)

enum class AppSort {
    APP_NAME, INSTALL_TIME, UPDATE_TIME, PACKAGE_NAME
}
