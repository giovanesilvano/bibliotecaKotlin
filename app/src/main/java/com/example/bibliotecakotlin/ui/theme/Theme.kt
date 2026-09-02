package com.exemplo.biblioteca.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta baseada nos mockups
val Primary = Color(0xFF3F51B5)
val PrimaryLight = Color(0xFFE8EAF6)
val Background = Color(0xFFF5F6FA)
val Surface = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1F2333)
val TextGray = Color(0xFF6B7280)
val TextLight = Color(0xFF9CA3AF)
val Border = Color(0xFFC5CAE9)
val Green = Color(0xFF2E7D32)
val GreenLight = Color(0xFFE6F4EA)
val Red = Color(0xFFD32F2F)
val RedLight = Color(0xFFFDECEC)
val Orange = Color(0xFFE65100)
val OrangeLight = Color(0xFFFFF3E0)

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    background = Background,
    surface = Surface,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun BibliotecaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}