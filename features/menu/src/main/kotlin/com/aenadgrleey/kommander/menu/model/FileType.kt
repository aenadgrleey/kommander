package com.aenadgrleey.kommander.menu.model

import com.aenadgrleey.kommander.core.utils.isKotlinFile
import okio.Path

sealed interface FileType {
    data object Kotlin : FileType
    data object Other : FileType
}

fun typeForFile(path: Path): FileType {
    return when  {
        path.isKotlinFile -> FileType.Kotlin
        else -> FileType.Other
    }
}