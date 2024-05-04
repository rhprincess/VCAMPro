package io.twinkle.vcampro.ui.viewmodel

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import io.twinkle.vcampro.ui.state.AppSort
import io.twinkle.vcampro.ui.state.AppsUiState
import io.twinkle.vcampro.ui.state.MainUiState
import io.twinkle.vcampro.vcampApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.Collator
import java.util.Locale

class AppsPageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    fun update(_update: (AppsUiState) -> AppsUiState) {
        _uiState.update(_update)
    }

    fun sortBy(way: AppSort) {
        val apps = uiState.value.apps
        val comparator = when (way) {
            AppSort.APP_NAME -> Comparators.byLabel
            AppSort.INSTALL_TIME -> Comparators.byInstallTime
            AppSort.UPDATE_TIME -> Comparators.byUpdateTime
            AppSort.PACKAGE_NAME -> Comparators.byPackageName
        }
        val sortedResult = apps.sortedWith(comparator)
        update { it.copy(apps = sortedResult, sortBy = way, dropDownMenuExpanded = false) }
    }

    fun reversed() {
        val apps = uiState.value.apps
        update { it.copy(apps = apps.reversed(), dropDownMenuExpanded = false) }
    }

    fun showDropDownMenu() {
        update { it.copy(dropDownMenuExpanded = true) }
    }

    fun closeDropDownMenu() {
        update { it.copy(dropDownMenuExpanded = false) }
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
    }
}