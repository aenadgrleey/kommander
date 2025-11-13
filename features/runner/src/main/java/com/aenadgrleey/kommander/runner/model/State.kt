package com.aenadgrleey.kommander.runner.model

import androidx.compose.ui.text.AnnotatedString
import com.aenadgrleey.kommander.core.utils.shell

data class State(
    val isRunning: Boolean,
    val command: String,
    val terminal: String,
    val output: List<AnnotatedString>,
){
    companion object {
        val Default = State(
            isRunning = false,
            terminal = shell.joinToString(" "),
            command = "",
            output = emptyList()
        )
    }
}
