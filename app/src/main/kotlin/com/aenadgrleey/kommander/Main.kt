package com.aenadgrleey.kommander

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.aenadgrleey.kommander.app_actions.AppActions
import com.aenadgrleey.kommander.core.compose.remember
import com.aenadgrleey.kommander.core.navigation.component
import com.aenadgrleey.kommander.core.theme.AppTheme
import com.aenadgrleey.kommander.core.utils.safe
import com.aenadgrleey.kommander.editor.EditorPane
import com.aenadgrleey.kommander.lsp.LspProcessor
import com.aenadgrleey.kommander.menu.MenuPane
import com.aenadgrleey.kommander.root.AppDependencies
import com.aenadgrleey.kommander.root.RootComponent
import com.aenadgrleey.kommander.root.RootPanes
import com.aenadgrleey.kommander.root.model.Project
import com.aenadgrleey.kommander.runner.RunnerPane
import okio.FileSystem

fun main(args: Array<String>) {
    val deps = safe { AppDependencies(LspProcessor(), FileSystem.SYSTEM) }
    if (deps == null) {
        println()
        println("Failed to initialize dependencies, exiting...")
        println("Check that your system has kotlin-language-server installed and is accessible in PATH")
        println()
        return
    }
    application {

        val rootComponent = component {
            RootComponent(
                args.firstOrNull(),
                lspProcessor = deps.lsp,
                fileSystem = deps.fs
            )
        }
        AppTheme {
            val windowState = rememberWindowState(
                placement = WindowPlacement.Maximized,
            )

            val state by rootComponent.state.collectAsState()
            val editingFileName by remember {
                derivedStateOf { (state.project as? Project.Editable)?.path?.name }
            }

            val title by derivedStateOf {
                if (editingFileName == null) "Kommander"
                else "Kommander ($editingFileName)"
            }



            Window(
                state = windowState,
                title = title,
                resizable = true,
                onCloseRequest = ::exitApplication,
            ) {
                RootPanes(
                    state = state,
                    onEvent = rootComponent::dispatch,
                    menu = {
                        MenuPane(
                            rootState = state,
                            onRootEvent = rootComponent::dispatch,
                            deps = deps
                        )
                    },
                    editor = {
                        EditorPane(
                            rootState = state,
                            onRootEvent = rootComponent::dispatch,
                            deps = deps
                        )
                    },
                    runner = {
                        RunnerPane(
                            rootState = state,
                            onRootEvent = rootComponent::dispatch
                        )
                    }
                )
                AppActions(
                    rootState = state,
                    onRootEvent = rootComponent::dispatch
                )
            }
        }

    }
}