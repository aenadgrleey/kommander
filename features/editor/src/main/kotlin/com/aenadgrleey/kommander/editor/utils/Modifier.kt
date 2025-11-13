package com.aenadgrleey.kommander.editor.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout

fun Modifier.heightPx(px: Int) = this.then(
    Modifier.layout { measurable, constraints ->
        // фиксируем высоту в px, остальное оставляем родителю
        val placeable = measurable.measure(
            constraints.copy(minHeight = px, maxHeight = px)
        )
        layout(placeable.width, px) {
            placeable.place(0, 0)
        }
    }
)

fun Modifier.interceptClick(
    onClick: (Offset) -> Unit
) =
    Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val down = event.changes.firstOrNull { it.changedToDownIgnoreConsumed() }
                if (down != null) {
                    onClick(down.position)
                    down.consume()
                }
            }
        }
    }