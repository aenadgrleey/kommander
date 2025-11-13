package com.aenadgrleey.kommander.editor.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextRange
import com.aenadgrleey.kommander.lsp.model.TextChange

sealed interface Event {
    data class OnTextFieldClick(val offset: Offset) : Event
    data object OnInputFocusLost : Event

    data class OnValueChange(
        val textChange: TextChange
    ) : Event

    data class OnEnterPressed(
        val cursor: TextRange,
        val withCtrl: Boolean
    ): Event

    data class OnBackspacePressed(
        val cursor: TextRange,
        val withCtrl: Boolean
    ): Event
}