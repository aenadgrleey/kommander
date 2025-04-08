package com.aenadgrleey.kommander.editor.model

import com.aenadgrleey.kommander.common.model.EditorCoordinates
import com.aenadgrleey.kommander.core.navigation.Effect

interface EditorEffect : Effect {
    data class NavigateToCode(val coordinates: EditorCoordinates) : EditorEffect
}