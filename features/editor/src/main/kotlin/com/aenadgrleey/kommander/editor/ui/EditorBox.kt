package com.aenadgrleey.kommander.editor.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
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
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    content: RowContent
) {
    val borderColor by animateColorAsState(
        targetValue =
            if (isHighlighted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
    )

    Box(
        modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.large)
            .border(3.dp, borderColor, MaterialTheme.shapes.large)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Row(
                Modifier
                    .height(IntrinsicSize.Min)
                    .width(IntrinsicSize.Max)
                    .padding(vertical = 6.dp)
                    .padding(start = 6.dp)
            ) {
                content()
            }
        }
    }
}