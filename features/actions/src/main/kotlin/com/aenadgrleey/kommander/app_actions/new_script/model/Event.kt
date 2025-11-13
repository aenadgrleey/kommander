package com.aenadgrleey.kommander.app_actions.new_script.model

sealed interface Event {
    data class UpdateDir(val path: String) : Event
    data class UpdateName(val name: String) : Event
    object Create : Event
    object Dismiss : Event
}