package com.aenadgrleey.kommander.menu.model

sealed interface MenuEvent {

    data class FileSelect(
        val projectNode: ProjectNode.FileNode
    ) : MenuEvent

    data class DirectoryOpen(
        val projectNode: ProjectNode.ClosedDirectoryNode
    ) : MenuEvent

    data class DirectoryClose(
        val projectNode: ProjectNode.OpenDirectoryNode
    ) : MenuEvent

}