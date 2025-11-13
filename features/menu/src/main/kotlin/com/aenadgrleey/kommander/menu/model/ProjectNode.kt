package com.aenadgrleey.kommander.menu.model

import okio.Path

sealed interface ProjectNode {
    sealed interface DirectoryNode : ProjectNode

    val name: String
    val path: Path

    data class ClosedDirectoryNode(
        override val name: String,
        override val path: Path
    ) : DirectoryNode

    data class OpenDirectoryNode(
        override val name: String,
        override val path: Path,
        val children: List<ProjectNode>
    ) : DirectoryNode

    data class FileNode(
        override val name: String,
        override val path: Path,
        val type: FileType,
    ) : ProjectNode

}