package io.twinkle.unreal.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.LocalLifecycleOwner


@Composable
fun Color.withTonalElevation(value: Dp): Color {
    return MaterialTheme.colorScheme.copy(surface = this).surfaceColorAtElevation(value)
}

@Composable
fun Material3SwitchColors(): SwitchColors = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.primary,
    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
    uncheckedThumbColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
    uncheckedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f)
)

fun Uri.toAbsoluteFilePath(context: Context): String {
    if (ContentResolver.SCHEME_FILE == this.scheme) {
        return this.path ?: ""
    } else if (ContentResolver.SCHEME_CONTENT == this.scheme) {
        val authority = this.authority
        if (authority!!.startsWith("com.android.externalstorage")) {
            return Environment.getExternalStorageDirectory()
                .toString() + "/" + this.path!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
        } else {
            var idStr = ""
            if (authority == "media") {
                idStr = this.toString().substring(this.toString().lastIndexOf('/') + 1)
            } else if (authority.startsWith("com.android.providers")) {
                idStr = DocumentsContract.getDocumentId(this).split(":".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1]
            }
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                arrayOf(MediaStore.Files.FileColumns.DATA),
                "_id=?",
                arrayOf(idStr),
                null
            )
            if (cursor != null) {
                cursor.moveToFirst()
                try {
                    val idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                    return cursor.getString(idx)
                } catch (e: java.lang.Exception) {
                } finally {
                    cursor.close()
                }
            }
        }
    }
    return ""
}
