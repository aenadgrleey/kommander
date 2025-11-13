package com.aenadgrleey.kommander.editor.model

import androidx.compose.ui.geometry.Offset
import com.aenadgrleey.kommander.core.navigation.Effect
import com.aenadgrleey.kommander.core.text.TextPosition

data class NavigateToCode(val position: TextPosition) : Effect
data class NavigateToNearestCode(val offset: Offset) : Effect
