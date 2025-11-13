package com.aenadgrleey.kommander.root.model

import com.aenadgrleey.kommander.core.navigation.PaneState

data class State(
    val project: Project,
    val runnerPaneState: PaneState,
    val menuPaneState: PaneState,
) {
    companion object {
        fun Init(project: Project) = State(
            project = project,
            runnerPaneState = PaneState.DefaultWide,
            menuPaneState = PaneState.DefaultThin,
        )
    }
}
