package com.example.cinemavault.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CinemaRed,
    background = CinemaBlack,
    surface = CinemaDarkGray,
    onPrimary = CinemaWhite,
    onBackground = CinemaWhite,
    onSurface = CinemaWhite
)

@Composable
fun CinemaVaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}