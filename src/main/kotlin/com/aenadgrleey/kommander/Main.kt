package com.aenadgrleey.kommander

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.aenadgrleey.kommander.core.async.runOnUiThread
import com.aenadgrleey.kommander.core.compose.collectAsState
import com.aenadgrleey.kommander.core.theme.AppTheme
import com.aenadgrleey.kommander.root.RootComponent
import com.aenadgrleey.kommander.root.RootPanes

fun main(args: Array<String>) {
    val rootComponent = runOnUiThread(::RootComponent)
    rootComponent.initWithPath(args.firstOrNull())
    application {
        DisposableEffect(Unit) {
            onDispose(rootComponent::clear)
        }
        AppTheme {
            val windowState = rememberWindowState(
                placement = WindowPlacement.Maximized,
            )
            val editingFileName by rootComponent.state
                .collectAsState { it.editingFile }

            val title by derivedStateOf {
                if (editingFileName == null) "Kommander"
                else "Kommander ($editingFileName)"
            }


            Window(
                state = windowState,
                title = title,
                resizable = true,
                onCloseRequest = ::exitApplication,
            ) { RootPanes(rootComponent) }

        }
    }
}
