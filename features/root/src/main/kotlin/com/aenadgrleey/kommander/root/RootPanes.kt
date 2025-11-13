package com.aenadgrleey.kommander.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.core.compose.ComposableContent
import com.aenadgrleey.kommander.core.navigation.Pane
import com.aenadgrleey.kommander.core.navigation.Panes
import com.aenadgrleey.kommander.core.navigation.PanesScope
import com.aenadgrleey.kommander.core.navigation.PositionInPane
import com.aenadgrleey.kommander.root.model.Event
import com.aenadgrleey.kommander.root.model.State

@Composable
fun RootPanes(
    state: State,
    onEvent: (Event) -> Unit,
    menu: ComposableContent,
    editor: @Composable PanesScope.() -> Unit,
    runner: ComposableContent,
) {
    Panes(Modifier.fillMaxSize()) {
        Pane(
            state = state.menuPaneState,
            minWidth = 100.dp,
            onRestate = { onEvent(Event.MenuRestate(it)) },
            handlePosition = PositionInPane.Right,
            expanded = { menu() },
        )
        editor()
        Pane(
            state = state.runnerPaneState,
            minWidth = 300.dp,
            handlePosition = PositionInPane.Left,
            expanded = { runner() },
            onRestate = { onEvent(Event.RunnerRestate(it)) }
        )
    }
}