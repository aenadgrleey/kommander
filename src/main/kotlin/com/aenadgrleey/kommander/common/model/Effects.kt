package com.aenadgrleey.kommander.common.model

import com.aenadgrleey.kommander.core.navigation.Effect
import java.io.File

data class OpenEditor(
    val coords: EditorCoordinates = EditorCoordinates.Default,
    val file: File,
) : Effect

typealias OnOpenEditor = (OpenEditor) -> Unit