package com.aenadgrleey.kommander.editor.utils

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import com.aenadgrleey.kommander.core.text.TextPosition

fun TextLayoutResult.targetY(
    position: TextPosition
): Int {
    val pureLine = (position.line - 1).coerceAtLeast(0)
    return getLineTop(pureLine).toInt()
}

fun TextLayoutResult.targetX(
    position: TextPosition
): Int {
    val pureLine = (position.line - 1).coerceAtLeast(0)
    return this.getLineRight(pureLine).toInt()
}


fun TextLayoutResult.targetCaret(
    textPosition: TextPosition
): TextRange {
    val pureLine = (textPosition.line - 1).coerceAtLeast(0)
    val lineStartsAtIndex = getLineStart(pureLine)
    val lineEndsAtIndex = getLineEnd(pureLine)
    return TextRange((lineStartsAtIndex + textPosition.column).coerceAtMost(lineEndsAtIndex))
}