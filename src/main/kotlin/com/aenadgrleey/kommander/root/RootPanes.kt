package com.aenadgrleey.kommander.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.aenadgrleey.kommander.core.navigation.Pane
import com.aenadgrleey.kommander.core.navigation.Panes
import com.aenadgrleey.kommander.core.navigation.PositionInPane
import com.aenadgrleey.kommander.editor.EditorPane
import com.aenadgrleey.kommander.menu.MenuPaneContent
import com.aenadgrleey.kommander.root.ui.Placeholder
import com.aenadgrleey.kommander.runner.RunnerPane

@Composable
fun RootPanes(rootComponent: RootComponent) {
    Panes(Modifier.fillMaxSize()) {
        val state by rootComponent.state.collectAsState()
        Pane(
            state = state.menuPaneState,
            onRestate = { rootComponent.menuRestate(it) },
            handlePosition = PositionInPane.Right,
            expanded = {
                MenuPaneContent(
                    menuComponent = rootComponent.menuComponent,
                    openEditor = { rootComponent.openEditor(it.coords, it.file) }
                )
            },
        )
        val editorComponent by rootComponent.editorComponent.collectAsState()

        if (editorComponent != null) {
            EditorPane(editorComponent!!)
        } else {
            Placeholder("No file opened")
        }
        Pane(
            state = state.runnerPaneState,
            handlePosition = PositionInPane.Left,
            expanded = {
                RunnerPane(
                    runnerComponent = rootComponent.runnerComponent,
                    openEditor = { rootComponent.openEditor(it.coords, it.file) }
                )
            },
            onRestate = { rootComponent.runnerRestate(it) }
        )
    }
}