package io.twinkle.unreal.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object Settings {
    const val TAG = "Settings"
    val IS_FORCE_SHOW_PERMISSION_ERR = booleanPreferencesKey("is_force_show_permission_err")
    val TEMPORARILY_DISABLE_MODULE = booleanPreferencesKey("temporarily_disable_module")
    val ENABLE_AUDIO = booleanPreferencesKey("enable_audio")
    val FORCE_USE_PRIVATE_DIR = booleanPreferencesKey("force_use_private_dir")
    val DISABLE_TOAST = booleanPreferencesKey("disable_toast")
    val APPS_SORT = stringPreferencesKey("apps_sort")
    val SHOW_SYSTEM_APPS = booleanPreferencesKey("show_system_apps")
    val SHOW_CAMERA_APPS = booleanPreferencesKey("show_camera_apps")
}

val Context.settings: DataStore<Preferences> by preferencesDataStore(name = Settings.TAG)