package com.aenadgrleey.kommander.core.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.core.compose.BoxContent
import java.awt.Cursor

data class PaneState(
    val visible: Boolean = true,
    val widthDp: Float,
) {
    companion object {
        val DefaultThin = PaneState(
            visible = true,
            widthDp = 200F
        )
        val DefaultWide = PaneState(
            visible = true,
            widthDp = 400F
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PanesScope.Pane(
    state: PaneState,
    minWidth: Dp,
    onRestate: (PaneState) -> Unit,
    handlePosition: PositionInPane,
    expanded: com.aenadgrleey.kommander.core.compose.ComposableContent,
) {
    if (state.visible) {
        var _state by remember(state) { mutableStateOf(state) }
        val density = LocalDensity.current
        var draggedWidth by remember { mutableStateOf(-1F) }
        LaunchedEffect(draggedWidth) {
            if (draggedWidth < 0F) return@LaunchedEffect
            val newWidthDp = with(density) { draggedWidth.toDp().value }
                .coerceAtLeast(minWidth.value)
            _state = _state.copy(widthDp = newWidthDp)
        }
        Box(
            Modifier
                .onGloballyPositioned { if (draggedWidth == -1F) draggedWidth = it.size.width.toFloat() }
                .width(IntrinsicSize.Min)
                .requiredWidth(_state.widthDp.dp)
                .widthIn(min = minWidth)
                .fillMaxHeight()
        ) {
            val handle: BoxContent = {
                Box(
                    Modifier
                        .align(
                            when (handlePosition) {
                                PositionInPane.Left -> Alignment.CenterStart
                                PositionInPane.Right -> Alignment.CenterEnd
                            }
                        )
                        .fillMaxHeight()
                        .width(10.dp)
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)), true)
                        .onDrag(
                            onDragCancel = { _state = state },
                            onDragEnd = { onRestate(_state) },
                            onDrag = {
                                when (handlePosition) {
                                    PositionInPane.Left -> draggedWidth -= it.x
                                    PositionInPane.Right -> draggedWidth += it.x
                                }
                            }
                        )
                ) {
                    Surface(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.extraLarge
                    ) { Box(Modifier.size(width = 4.dp, height = 32.dp)) }
                }
            }
            expanded()
            handle()
        }
    } else Spacer(Modifier.width(20.dp))
}

enum class PositionInPane { Left, Right }
