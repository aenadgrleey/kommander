package com.aenadgrleey.kommander.root

import com.aenadgrleey.kommander.core.navigation.MVIComponent
import com.aenadgrleey.kommander.core.text.TextPosition
import com.aenadgrleey.kommander.core.utils.firstKotlinFile
import com.aenadgrleey.kommander.core.utils.orElse
import com.aenadgrleey.kommander.lsp.LspProcessor
import com.aenadgrleey.kommander.root.model.Event
import com.aenadgrleey.kommander.root.model.Project
import com.aenadgrleey.kommander.root.model.Project.Directory
import com.aenadgrleey.kommander.root.model.Project.SingleFile
import com.aenadgrleey.kommander.root.model.State
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path.Companion.toPath

class RootComponent(
    initPath: String?,
    private val lspProcessor: LspProcessor,
    val fileSystem: FileSystem
) : MVIComponent<State, Event>() {


    override val defaultState = State.Init(
        project = projectFromPath(
            initPath?.toPath(normalize = true)
        )
    )

    init {
        coroutineScope.launch {
            initPath?.let { lspProcessor.initialize(it) }
        }
    }

    override fun dispatch(event: Event) {
        when (event) {
            is Event.InitProject -> {
                val prevState = mState.getAndUpdate { it.copy(project = projectFromPath(event.path)) }
                if (prevState.project !is Project.Nothing) lspProcessor.shutdown()
                coroutineScope.launch {
                    lspProcessor.initialize(event.path.toString())
                }
            }

            is Event.OpenProjectFile -> {
                mState.update {
                    when (val current = it.project) {
                        is SingleFile -> it.copy(
                            project = SingleFile(
                                event.path,
                                event.position
                            )
                        )

                        is Directory -> it.copy(
                            project = Directory(
                                current.dir,
                                path = event.path,
                                event.position
                            )
                        )

                        else -> it
                    }
                }
            }

            is Event.MenuRestate -> mState.update { it.copy(menuPaneState = event.paneState) }
            is Event.RunnerRestate -> mState.update { it.copy(runnerPaneState = event.paneState) }
        }
    }

    private fun projectFromPath(path: okio.Path?): Project {
        val metadata = path?.let { fileSystem.metadataOrNull(it) }
        return when {
            path == null || metadata == null -> Project.Nothing
            metadata.isRegularFile ->
                SingleFile(path, TextPosition.Undefined)

            metadata.isDirectory ->
                fileSystem.firstKotlinFile(path)
                    ?.let {
                        Directory(path, it, TextPosition.Undefined)
                    }.orElse {
                        Project.EmptyDirectory(path)
                    }
            else ->
                Project.Nothing
        }
    }
}