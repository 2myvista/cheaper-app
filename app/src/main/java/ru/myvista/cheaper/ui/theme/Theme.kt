package ru.myvista.cheaper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GraphiteGreen = Color(0xFF263B35)
private val LightGray = Color(0xFFF2F4F3)
private val White = Color(0xFFFFFFFF)
private val DarkText = Color(0xFF263B35)
private val MutedGreen = Color(0xFFE0F1E7)
private val ResultGreen = Color(0xFF185C39)

private val LightColorScheme = lightColorScheme(
    primary = GraphiteGreen,
    onPrimary = White,
    secondary = GraphiteGreen,
    onSecondary = White,
    tertiary = ResultGreen,
    onTertiary = White,
    background = LightGray,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = Color(0xFFE4E9E6),
    onSurfaceVariant = Color(0xFF56645D),
    outline = Color(0xFFB8C4BD)
)


private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA9C9B8),
    onPrimary = Color(0xFF19352A),

    secondary = Color(0xFFB8CFC1),
    onSecondary = Color(0xFF19352A),

    tertiary = Color(0xFFB5DCC2),
    onTertiary = Color(0xFF17452D),

    background = Color(0xFF171D1A),
    onBackground = Color(0xFFE1E8E3),

    surface = Color(0xFF202823),
    onSurface = Color(0xFFE1E8E3),

    surfaceVariant = Color(0xFF354139),
    onSurfaceVariant = Color(0xFFC1CEC5),

    outline = Color(0xFF829087)
)


@Composable
fun ЧтоДешевлеTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun appTextFieldColors(): TextFieldColors {
    val colors = MaterialTheme.colorScheme

    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.onSurface,
        unfocusedTextColor = colors.onSurface,
        disabledTextColor = colors.onSurfaceVariant,

        focusedLabelColor = colors.primary,
        unfocusedLabelColor = colors.onSurfaceVariant,
        disabledLabelColor = colors.onSurfaceVariant,

        cursorColor = colors.primary,

        focusedBorderColor = colors.primary,
        unfocusedBorderColor = colors.outline,
        disabledBorderColor = colors.outline,

        focusedContainerColor = colors.surface,
        unfocusedContainerColor = colors.surface,
        disabledContainerColor = colors.surface
    )
}


