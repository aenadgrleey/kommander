package com.aenadgrleey.kommander.menu.model

import com.aenadgrleey.kommander.common.model.FileType
import java.io.File

sealed interface ProjectNode {
    sealed interface DirectoryNode : ProjectNode

    val name: String
    val file: File

    data class ClosedDirectoryNode(
        override val name: String,
        override val file: File
    ) : DirectoryNode

    data class OpenDirectoryNode(
        override val name: String,
        override val file: File,
        val children: List<ProjectNode>
    ) : DirectoryNode

    data class FileNode(
        override val name: String,
        override val file: File,
        val type: FileType,
    ) : ProjectNode

}