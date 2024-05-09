package io.twinkle.unreal.ui.viewmodel

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.twinkle.unreal.data.EnableMap
import io.twinkle.unreal.ui.state.AppSort
import io.twinkle.unreal.ui.state.AppsUiState
import io.twinkle.unreal.util.Settings
import io.twinkle.unreal.util.settings
import io.twinkle.unreal.vcampApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.text.Collator
import java.util.Locale

class AppsPageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    fun update(_update: (AppsUiState) -> AppsUiState) {
        _uiState.update(_update)
    }

    fun sortBy(way: AppSort) {
        val apps =
            if (uiState.value.showCameraApps) uiState.value.cameraApps else uiState.value.apps
        val sortedResult = when (way) {
            AppSort.APP_NAME -> apps.sortedWith(Comparators.byLabel)
            AppSort.INSTALL_TIME -> apps.sortedWith(Comparators.byInstallTime)
            AppSort.UPDATE_TIME -> apps.sortedWith(Comparators.byUpdateTime)
            AppSort.PACKAGE_NAME -> apps.sortedWith(Comparators.byPackageName)
        }
        viewModelScope.launch {
            vcampApp.settings.edit {
                it[Settings.APPS_SORT] = way.name
            }
        }
        update {
            it.copy(
                appsShowed = sortedResult.sortedWith(Comparators.byEnabled(uiState.value.enableMap)),
                sortBy = way,
                dropDownMenuExpanded = false
            )
        }
    }

    fun reversed() {
        val apps = uiState.value.appsShowed
        update { it.copy(appsShowed = apps.reversed(), dropDownMenuExpanded = false) }
    }

    fun showDropDownMenu() {
        update { it.copy(dropDownMenuExpanded = true) }
    }

    fun closeDropDownMenu() {
        update { it.copy(dropDownMenuExpanded = false) }
    }

    fun refresh() {
        update { it.copy(isRefreshing = true) }
        val apps = vcampApp.packageManager.getInstalledPackages(
            PackageManager.GET_PERMISSIONS or PackageManager.GET_CONFIGURATIONS
        )
        val cameraApps = apps.filter {
            if (it.requestedPermissions != null) it.requestedPermissions.contains(
                android.Manifest.permission.CAMERA
            ) else false
        }

        val jsonFile =
            File(vcampApp.filesDir.path + "/enable_list.json")
        val enableMap =
            if (jsonFile.exists()) Json.decodeFromString<EnableMap>(jsonFile.readText()) else EnableMap()

        viewModelScope.launch {
            vcampApp.settings.data.collect { pref ->
                update {
                    it.copy(
                        apps = apps, cameraApps = cameraApps, appsShowed = apps,
                        showSystemApp = pref[Settings.SHOW_SYSTEM_APPS] ?: false,
                        showCameraApps = pref[Settings.SHOW_CAMERA_APPS] ?: false,
                        enableMap = enableMap,
                        isRefreshing = false
                    )
                }
                sortBy(
                    AppSort.valueOf(
                        pref[Settings.APPS_SORT] ?: AppSort.UPDATE_TIME.name
                    )
                )
            }
        }
    }

    private object Comparators {

        private val pm: PackageManager = vcampApp.packageManager
        val byLabel = Comparator<PackageInfo> { o1, o2 ->
            val n1 = o1.applicationInfo.loadLabel(pm).toString().lowercase(Locale.getDefault())
            val n2 = o2.applicationInfo.loadLabel(pm).toString().lowercase(Locale.getDefault())
            Collator.getInstance(Locale.getDefault()).compare(n1, n2)
        }
        val byPackageName = Comparator<PackageInfo> { o1, o2 ->
            val n1 = o1.packageName.lowercase(Locale.getDefault())
            val n2 = o2.packageName.lowercase(Locale.getDefault())
            Collator.getInstance(Locale.getDefault()).compare(n1, n2)
        }
        val byInstallTime = Comparator<PackageInfo> { o1, o2 ->
            val n1 = o1.firstInstallTime
            val n2 = o2.firstInstallTime
            n2.compareTo(n1)
        }
        val byUpdateTime = Comparator<PackageInfo> { o1, o2 ->
            val n1 = o1.lastUpdateTime
            val n2 = o2.lastUpdateTime
            n2.compareTo(n1)
        }

        fun byEnabled(enableMap: EnableMap) = Comparator<PackageInfo> { o1, o2 ->
            val n1 = enableMap.map[o1.applicationInfo.packageName] ?: false
            val n2 = enableMap.map[o2.applicationInfo.packageName] ?: false
            n2.compareTo(n1)
        }

    }

}