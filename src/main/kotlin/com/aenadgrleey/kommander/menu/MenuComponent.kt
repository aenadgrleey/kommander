package com.aenadgrleey.kommander.menu

import com.aenadgrleey.kommander.common.model.OpenEditor
import com.aenadgrleey.kommander.common.model.typeForFile
import com.aenadgrleey.kommander.core.navigation.MVIComponent
import com.aenadgrleey.kommander.menu.model.MenuEvent
import com.aenadgrleey.kommander.menu.model.MenuState
import com.aenadgrleey.kommander.menu.model.ProjectNode
import kotlinx.coroutines.flow.update
import java.io.File
import kotlin.coroutines.CoroutineContext

class MenuComponent(
    coroutineContext: CoroutineContext
) : MVIComponent<MenuState, MenuEvent>(coroutineContext) {

    override val defaultState = MenuState.Default
    override fun dispatch(event: MenuEvent) {
        when (event) {
            is MenuEvent.FileSelect -> effect(OpenEditor(file = event.projectNode.file))
            is MenuEvent.ProjectOpen -> {
                mState.update {
                    val project = when {
                        event.project.isDirectory -> ProjectNode.OpenDirectoryNode(
                            event.project.name,
                            event.project,
                            buildChildrenNodes(event.project)
                        )

                        event.project.isFile -> ProjectNode.FileNode(
                            event.project.name,
                            event.project,
                            typeForFile(event.project)
                        )
                        else -> return
                    }
                    it.copy(listOf(project))
                }
            }

            MenuEvent.ProjectClose -> mState.update { it.copy(items = emptyList()) }
            is MenuEvent.DirectoryClose -> mState.update {
                it.copy(
                    items = it.items.map { node ->
                        if (node != event.projectNode) node
                        else {
                            ProjectNode.ClosedDirectoryNode(
                                node.name,
                                node.file
                            )
                        }
                    }
                )
            }
            is MenuEvent.DirectoryOpen -> mState.update {
                it.copy(
                    items = it.items.map { node ->
                        if (node != event.projectNode) node
                        else {
                            ProjectNode.OpenDirectoryNode(
                                node.name,
                                node.file,
                                buildChildrenNodes(node.file)
                            )
                        }
                    }
                )
            }
        }
    }

    private fun buildChildrenNodes(file: File): List<ProjectNode> {
        return file.listFiles()?.map { child ->
            if (child.isDirectory) {
                ProjectNode.ClosedDirectoryNode(child.name, child)
            } else {
                ProjectNode.FileNode(child.name, child, typeForFile(child))
            }
        } ?: emptyList()
    }
}