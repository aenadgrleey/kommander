package com.aenadgrleey.kommander.runner

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.aenadgrleey.kommander.common.model.EditorCoordinates
import com.aenadgrleey.kommander.common.model.OpenEditor
import com.aenadgrleey.kommander.core.navigation.MVIComponent
import com.aenadgrleey.kommander.core.utils.isKotlinFile
import com.aenadgrleey.kommander.core.utils.parentDir
import com.aenadgrleey.kommander.core.utils.shell
import com.aenadgrleey.kommander.runner.model.RunnerEvent
import com.aenadgrleey.kommander.runner.model.RunnerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.update
import java.io.File
import kotlin.coroutines.CoroutineContext

class RunnerComponent(
    coroutineContext: CoroutineContext
) : MVIComponent<RunnerState, RunnerEvent>(coroutineContext) {
    private var processJob: Job? = null

    override val defaultState = RunnerState.Default

    override fun dispatch(event: RunnerEvent) {
        when (event) {
            is RunnerEvent.InputCommand -> mState.update { it.copy(command = event.command) }
            is RunnerEvent.StartProcess -> {
                val hasActiveJob = processJob?.isActive == true
                val command = state.value.command
                File.listRoots().first()
                if (!hasActiveJob && !state.value.isRunning && command.isNotEmpty()) {
                    mState.update { it.copy(isRunning = true, output = emptyList()) }
                    processJob = coroutine {
                        val process = ProcessBuilder()
                            .command(shell + command)
                            .redirectErrorStream(true)
                            .start()

                        val reader = process.inputStream.bufferedReader()

                        while (true) {
                            val line = reader.readLine() ?: break
                            ensureActive()
                            mState.update { it.copy(output = it.output + parseOutput(line)) }
                        }

                        process.waitFor()
                        mState.update { it.copy(isRunning = false) }
                    }
                }
            }

            is RunnerEvent.StopProcess -> {
                mState.update { it.copy(isRunning = false) }
                processJob?.cancel()
            }

        }
    }

    private fun parseOutput(text: String): AnnotatedString {
        val matchResult = errorRegex.find(text)
        return if (matchResult == null)
            AnnotatedString(text)
        else buildAnnotatedString {
            val (fileRel, lineNum, colNum) = matchResult.destructured
            val file = File(parentDir, fileRel)
            val clickable = LinkAnnotation.Clickable(
                tag = "$lineNum|$colNum|${file.absolutePath}",
                styles = TextLinkStyles(errorOutputSpan),
                linkInteractionListener = LinkInteractionListener {
                    val (line, column, filePath) = (it as LinkAnnotation.Clickable).tag.split("|")
                    effect(OpenEditor(EditorCoordinates(line.toInt(), column.toInt()), File(filePath)))
                }
            )
            withLink(clickable) { append(text) }
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
        mState.update { it.copy(output = it.output.map { parseOutput(it.text) }) }
    }

    fun presetCommandForKotlinFile(file: File) {
        if (file.isKotlinFile) {
            dispatch(RunnerEvent.StopProcess)
            dispatch(RunnerEvent.InputCommand("kotlinc -script ${file.absolutePath}"))
        }
    }

    companion object {
        private val errorRegex = Regex("""^(.+\.kt(?:s)?):(\d+):(\d+): error: (.+)""")
    }
}