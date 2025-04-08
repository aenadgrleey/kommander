package com.aenadgrleey.kommander.root.model

import com.aenadgrleey.kommander.core.navigation.PaneState

data class RootState(
    val editingFile: String?,
    val runnerPaneState: PaneState,
    val menuPaneState: PaneState,
) {
    companion object {
        val Default = RootState(
            editingFile = null,
            runnerPaneState = PaneState(visible = true, widthDp = 400f),
            menuPaneState = PaneState.Default,
        )
    }
}
