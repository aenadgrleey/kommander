package com.aenadgrleey.kommander.editor.model

import com.aenadgrleey.kommander.lsp.model.Token


sealed interface State {

    val canEdit: Boolean

    data class Loading(
        override val canEdit: Boolean,
    ) : State

    data class Ready(
        val lines: Lines,
        val tokens: Tokens,
        override val canEdit: Boolean,
    ) : State {
        constructor(
            lines: List<String>,
            tokens: List<Token>,
            canEdit: Boolean,
        ) : this(
            lines = Lines(lines),
            tokens = Tokens(tokens),
            canEdit = canEdit,
        )
    }

    companion object {
        fun Init(canEdit: Boolean) = Loading(canEdit)
    }
}

