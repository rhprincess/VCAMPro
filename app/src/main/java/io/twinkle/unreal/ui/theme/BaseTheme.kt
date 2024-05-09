package io.twinkle.unreal.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

interface BaseTheme {
    val lightScheme: ColorScheme
    val darkScheme: ColorScheme
    val mediumContrastLightColorScheme: ColorScheme
        get() = lightScheme
    val highContrastLightColorScheme: ColorScheme
        get() = lightScheme
    val mediumContrastDarkColorScheme: ColorScheme
        get() = darkScheme
    val highContrastDarkColorScheme: ColorScheme
        get() = darkScheme

    @Immutable
    data class ColorFamily(
        private val color: Color,
        private val onColor: Color,
        private val colorContainer: Color,
        private val onColorContainer: Color
    )

    val unspecifiedScheme: ColorFamily
        get() = ColorFamily(
            Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
        )
}