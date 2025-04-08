package com.aenadgrleey.kommander.editor.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.core.compose.RowContent

@Composable
fun EditorBox(
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
    content: RowContent
) {
    val borderColor by animateColorAsState(
        targetValue =
            if (isHighlighted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
    )

    Row(
        modifier
            .clip(MaterialTheme.shapes.large)
            .border(3.dp, borderColor, MaterialTheme.shapes.large)
    ) { content() }
}