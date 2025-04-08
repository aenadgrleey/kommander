package com.aenadgrleey.kommander.core.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.core.compose.conditional

@Composable
fun ThemedPreview(
    hasBackground: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    @Composable
    fun ThemedContent() {
        Column(
            Modifier
                .conditional(hasBackground) { Modifier.background(MaterialTheme.colors.surface) }
                .padding(16.dp)
        ) {
            content.invoke(this)
        }
    }

    AppTheme { ThemedContent() }
}
