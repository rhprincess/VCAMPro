package io.twinkle.vcampro.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

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