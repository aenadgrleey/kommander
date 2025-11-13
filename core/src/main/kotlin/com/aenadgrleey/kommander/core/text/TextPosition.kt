package com.aenadgrleey.kommander.core.text

data class TextPosition(
    val line: Int,
    val column: Int,
) {
    companion object {
        val Undefined = TextPosition(-1, -1)
    }
}

val TextPosition.isDefined: Boolean
    get() = this != TextPosition.Undefined