package com.aenadgrleey.kommander.runner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.core.navigation.component
import com.aenadgrleey.kommander.root.model.Event
import com.aenadgrleey.kommander.root.model.OpenFileEffect
import com.aenadgrleey.kommander.root.model.State
import com.aenadgrleey.kommander.runner.ui.RunnerController
import com.aenadgrleey.kommander.runner.ui.RunnerOutput

@Composable
fun RunnerPane(
    rootState: State,
    onRootEvent: (Event) -> Unit,
) {
    val component = component { RunnerComponent() }
    LaunchedEffect(component) {
        component.effect.collect {
            when (it) {
                is OpenFileEffect -> onRootEvent(Event.OpenProjectFile(it.path))
            }
        }
    }

    val state by component.state.collectAsState()


    val highlight = MaterialTheme.colorScheme.error
    LaunchedEffect(highlight) {
        component.bindHighlightColor(highlight)
    }

    Column {
        Text(
            text = "Runner",
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 22.dp,
                    bottom = 10.dp
                )
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                )
        )

        RunnerController(
            command = state.command,
            terminal = state.terminal,
            isRunning = state.isRunning,
            onEvent = component::dispatch
        )

        RunnerOutput(
            output = state.output,
            onEvent = component::dispatch
        )
    }
}