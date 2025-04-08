package com.aenadgrleey.kommander.runner.model

sealed interface RunnerEvent {
    data object StartProcess : RunnerEvent
    data object StopProcess : RunnerEvent
    data class InputCommand(
        val command: String
    ) : RunnerEvent
}
