package com.despreschen.mygoodaddresses.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/** The app's existing palette, carried over from the XML theme. */
private val Orange = Color(0xFFFB6107)
private val Citrine = Color(0xFFF3DE2C)
private val Xanthous = Color(0xFFFBB02D)
private val AppleGreen = Color(0xFF7CB518)

private val LightColors = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    secondary = Xanthous,
    onSecondary = Color.Black,
    tertiary = AppleGreen,
)

/**
 * Orange is too dark to sit on a dark surface as a primary, so the light steps
 * of the same palette take over rather than reusing the day values.
 */
private val DarkColors = darkColorScheme(
    primary = Xanthous,
    onPrimary = Color(0xFF3A1A00),
    secondary = Citrine,
    onSecondary = Color.Black,
    tertiary = AppleGreen,
)

@Composable
fun MyGoodAddressesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    /** Material You colours, available from Android 12 onwards. */
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
