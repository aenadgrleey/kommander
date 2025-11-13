package com.aenadgrleey.kommander.editor.model

import androidx.compose.runtime.Immutable
import com.aenadgrleey.kommander.lsp.model.Token

@Immutable
data class Tokens(
    val tokens: List<Token>
) : List<Token> by tokens

@Immutable
data class Lines(
    val lines: List<String>
) : List<String> by lines