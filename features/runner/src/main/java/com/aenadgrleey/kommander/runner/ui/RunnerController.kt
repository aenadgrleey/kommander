package com.aenadgrleey.kommander.runner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Enter
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.runner.model.Event

@Composable
fun RunnerController(
    command: String,
    terminal: String,
    isRunning: Boolean,
    onEvent: (Event) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TextField(
            value = command,
            label = { Text(text = terminal) },
            singleLine = true,
            maxLines = 1,
            modifier = Modifier
                .onKeyEvent {
                    if (it.key == Enter) {
                        onEvent(Event.StartProcess)
                        true
                    } else false
                }
                .weight(1f),
            enabled = !isRunning,
            readOnly = isRunning,
            onValueChange = {
                onEvent(Event.InputCommand(it))
            }
        )

        FilledIconButton(
            enabled = !isRunning,
            colors = IconButtonDefaults.filledTonalIconButtonColors(),
            onClick = { onEvent(Event.StartProcess) }
        ) {
            Icon(
                imageVector = Icons.Sharp.PlayArrow,
                contentDescription = null
            )
        }

        FilledIconButton(
            enabled = isRunning,
            colors = IconButtonDefaults.filledIconButtonColors(),
            onClick = { onEvent(Event.StopProcess) }
        ) {
            Icon(
                imageVector = Icons.Sharp.Close,
                contentDescription = null
            )
        }


    }
}