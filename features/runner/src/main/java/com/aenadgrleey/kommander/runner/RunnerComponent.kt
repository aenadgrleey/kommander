package com.aenadgrleey.kommander.runner

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.aenadgrleey.kommander.core.navigation.MVIComponent
import com.aenadgrleey.kommander.core.utils.isKotlinFile
import com.aenadgrleey.kommander.core.utils.parentDir
import com.aenadgrleey.kommander.core.utils.shell
import com.aenadgrleey.kommander.runner.model.Event
import com.aenadgrleey.kommander.runner.model.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import java.io.File

class RunnerComponent : MVIComponent<State, Event>() {
    private var processJob: Job? = null

    override val defaultState = State.Default

    override fun dispatch(event: Event) {
        when (event) {
            is Event.TextLinkClick -> {
                when {
                    event.tag == "error" -> {

                    }
                }
            }
            is Event.InputCommand -> mState.update { it.copy(command = event.command) }
            is Event.StartProcess -> {
                val hasActiveJob = processJob?.isActive == true
                val command = state.value.command
                File.listRoots().first()
                if (!hasActiveJob && !state.value.isRunning && command.isNotEmpty()) {
                    mState.update { it.copy(isRunning = true, output = emptyList()) }
                    processJob = coroutine {
                        val process = ProcessBuilder()
                            .command(shell + command)
                            .start()

                        process.inputStream.bufferedReader()
                        process.errorStream.bufferedReader()

                        TODO()

                        process.waitFor()
                        mState.update { it.copy(isRunning = false) }
                    }
                }
            }

            is Event.StopProcess -> {
                mState.update { it.copy(isRunning = false) }
                processJob?.cancel()
            }

        }
    }

    @OptIn(ExperimentalTextApi::class)
    private fun parseErrorOutput(text: String): AnnotatedString {
        val matchResult = errorRegex.find(text)
        return if (matchResult == null)
            AnnotatedString(text)
        else buildAnnotatedString {
            val (fileRel, lineNum, colNum) = matchResult.destructured
            val path = parentDir.resolve(fileRel)

            withAnnotation(
                tag = "error",
                annotation = "$lineNum|$colNum|$path"
            ) { append(text) }
        }
    }

    private var errorOutputSpan = SpanStyle(
        color = Color.Red,
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline,
    )

    fun bindHighlightColor(color: Color) {
        errorOutputSpan = errorOutputSpan.copy(color = color)
        // reapply color to all output
        mState.update { it.copy(output = it.output.map { parseErrorOutput(it.text) }) }
    }

    fun presetCommandForKotlinFile(file: File) {
        if (file.isKotlinFile) {
            dispatch(Event.StopProcess)
            dispatch(Event.InputCommand("kotlinc -script ${file.absolutePath}"))
        }
    }

    companion object {
        private val errorRegex = Regex("""^(.+\.kt(?:s)?):(\d+):(\d+): error: (.+)""")
    }
}