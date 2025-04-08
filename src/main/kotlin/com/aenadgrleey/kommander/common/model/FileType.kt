package com.aenadgrleey.kommander.common.model

import java.io.File

sealed interface FileType {
    data object Kotlin : FileType
    data object Other : FileType
}

fun typeForFile(file: File) = when {
    file.extension == "kt" -> FileType.Kotlin
    file.extension == "kts" -> FileType.Kotlin
    else -> FileType.Other
}