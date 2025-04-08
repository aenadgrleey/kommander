package com.aenadgrleey.kommander.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

interface PanesScope {
    fun Modifier.fillPanesWidth(): Modifier
}

internal class PaneScopeInstance(
    private val rowScope: RowScope
) : PanesScope {
    override fun Modifier.fillPanesWidth() =
        with(rowScope) { Modifier.weight(1f) }
}

@Composable
fun Panes(
    modifier: Modifier = Modifier,
    content: @Composable PanesScope.() -> Unit
) {
    Row(
        modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        val scope = remember(this) { PaneScopeInstance(this) }
        content(scope)
    }
}