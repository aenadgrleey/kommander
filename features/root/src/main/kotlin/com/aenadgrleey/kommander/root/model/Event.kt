package com.aenadgrleey.kommander.root.model

import com.aenadgrleey.kommander.core.navigation.PaneState
import com.aenadgrleey.kommander.core.text.TextPosition
import okio.Path

sealed interface Event {
    data class MenuRestate(val paneState: PaneState) : Event
    data class RunnerRestate(val paneState: PaneState) : Event
    data class InitProject(val path: Path) : Event
    data class OpenProjectFile(
        val path: Path,
        val position: TextPosition = TextPosition.Undefined
    ) : Event
}