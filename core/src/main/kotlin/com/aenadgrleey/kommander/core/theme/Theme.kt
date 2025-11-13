package com.aenadgrleey.kommander.core.theme

import AppTypography
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun AppTheme(
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        typography = AppTypography,
        content = content
    )
}

