package com.aenadgrleey.kommander.root.model

import com.aenadgrleey.kommander.core.text.TextPosition
import okio.Path

sealed interface Project {
    data object Nothing : Project
    data class EmptyDirectory(override val dir: Path) : Project, Navigable
    data class SingleFile(
        override val path: Path,
        override val initPosition: TextPosition
    ) : Project, Editable

    data class Directory(
        override val dir: Path,
        override val path: Path,
        override val initPosition: TextPosition,
    ) : Project, Editable, Navigable

    interface Editable {
        val path: Path
        val initPosition: TextPosition
    }

    sealed interface Navigable {
        val dir: Path
    }
}