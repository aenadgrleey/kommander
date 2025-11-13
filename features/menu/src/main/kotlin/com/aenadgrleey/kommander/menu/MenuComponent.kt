package com.aenadgrleey.kommander.menu

import com.aenadgrleey.kommander.core.navigation.MVIComponent
import com.aenadgrleey.kommander.menu.model.MenuEvent
import com.aenadgrleey.kommander.menu.model.ProjectNode
import com.aenadgrleey.kommander.menu.model.State
import com.aenadgrleey.kommander.menu.model.typeForFile
import com.aenadgrleey.kommander.root.model.OpenFileEffect
import com.aenadgrleey.kommander.root.model.Project
import kotlinx.coroutines.flow.update
import okio.FileSystem
import okio.Path

class MenuComponent(
    project: Project.Navigable,
    private val fileSystem: FileSystem
) : MVIComponent<State, MenuEvent>() {

    override val defaultState = State(
        items = buildChildrenNodes(project.dir)
    )

    override fun dispatch(event: MenuEvent) {
        when (event) {
            is MenuEvent.FileSelect -> effect(OpenFileEffect(event.projectNode.path))
            is MenuEvent.DirectoryClose -> mState.update {
                it.copy(
                    items = it.items.map { node ->
                        if (node != event.projectNode) node
                        else {
                            ProjectNode.ClosedDirectoryNode(
                                node.name,
                                node.path
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
                                node.path,
                                children = buildChildrenNodes(node.path)
                            )
                        }
                    }
                )
            }
        }
    }

    private fun buildChildrenNodes(path: Path) =
        fileSystem
            .listOrNull(path)
            ?.map { child ->
                val md = fileSystem.metadata(child)
                if (md.isDirectory) ProjectNode.ClosedDirectoryNode(child.name, child)
                else ProjectNode.FileNode(child.name, child, typeForFile(child))
            }.orEmpty()
}