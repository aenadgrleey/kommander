package com.aenadgrleey.kommander.lsp.model

import com.aenadgrleey.kommander.editor.lsp.TokenType

data class Token(
    val line: Int,
    val offset: Int,
    val length: Int,
    val type: TokenType,
)

fun List<Int>.toTokens(
    legend: List<TokenType>
) = buildList {
    var line = 0
    var char = 0
    var i = 0

    while (i + 4 < size) {
        val deltaLine = this@toTokens[i]
        val deltaChar = this@toTokens[i + 1]
        val length = this@toTokens[i + 2]
        val tokenTypeIndex = this@toTokens[i + 3]

        line += deltaLine
        if (deltaLine > 0) char = 0
        char += deltaChar


        val type = legend.getOrElse(tokenTypeIndex) { TokenType.Unknown }
        add(Token(line, char, length, type))
        i += 5
    }
}