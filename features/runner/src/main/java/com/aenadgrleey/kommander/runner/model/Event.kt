package com.aenadgrleey.kommander.runner.model

sealed interface Event {
    data object StartProcess : Event
    data object StopProcess : Event
    data class InputCommand(
        val command: String
    ) : Event
    data class TextLinkClick(
        val tag: String,
        val annotation: String
    ): Event
}
