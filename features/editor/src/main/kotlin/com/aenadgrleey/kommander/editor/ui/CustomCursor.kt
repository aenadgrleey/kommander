package com.aenadgrleey.kommander.editor.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

@Composable
fun CustomCursor(
    layoutResult: TextLayoutResult?,
    textFieldValue: TextFieldValue,
) {
    if (layoutResult != null) {
        var showCursor by remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            while (true) {
                delay(500)
                showCursor = !showCursor
            }
        }

        val color = MaterialTheme.colorScheme.primary
        Canvas(Modifier.zIndex(10f).fillMaxSize()) {
            val rect = layoutResult.getCursorRect(textFieldValue.selection.end)
            if (showCursor) {
                drawRect(
                    color = color,
                    topLeft = rect.topLeft,
                    size = Size(
                        width = 2.5.dp.toPx(),
                        height = rect.height
                    )
                )
            }
        }
    }
}