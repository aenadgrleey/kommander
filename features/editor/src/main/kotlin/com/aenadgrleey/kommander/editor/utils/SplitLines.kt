package com.aenadgrleey.kommander.editor.utils

fun splitLines(text: String): List<String> {
    if (text.isEmpty()) return emptyList()
    return text.split(Regex("\\r?\\n|\\r"))
}
