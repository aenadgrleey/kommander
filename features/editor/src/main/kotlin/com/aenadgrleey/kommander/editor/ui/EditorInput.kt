package com.aenadgrleey.kommander.editor.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.core.compose.remember
import com.aenadgrleey.kommander.editor.lsp.TokenType
import com.aenadgrleey.kommander.editor.model.Event
import com.aenadgrleey.kommander.editor.model.Lines
import com.aenadgrleey.kommander.editor.model.State
import com.aenadgrleey.kommander.editor.model.Tokens
import com.aenadgrleey.kommander.editor.utils.interceptClick
import com.aenadgrleey.kommander.lsp.model.TextChange
import kotlin.math.abs

@Composable
fun EditorInput(
    state: State.Ready,
    onEvent: (Event) -> Unit,
    textLayoutResult: MutableState<TextLayoutResult?>,
    textFieldValue: MutableState<TextFieldValue>,
    focusRequester: FocusRequester,
    horizontalScrollState: ScrollState,
) {
    val textStyle = MaterialTheme.typography.bodyLarge.remember {
        it.copy(
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.None
            )
        )
    }

    LineNumbers(
        lineHeight = textStyle.lineHeight,
        lineHeightStyle = textStyle.lineHeightStyle,
        layoutResult = textLayoutResult.value
    )

    // Divider
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 8.dp, end = 4.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(0.5f))
    )

    val colorScheme =
        TokenColorScheme(
            keyword = MaterialTheme.colorScheme.primary,
            function = MaterialTheme.colorScheme.secondary,
            variable = MaterialTheme.colorScheme.tertiary,
            comment = MaterialTheme.colorScheme.outline,
            default = MaterialTheme.colorScheme.onSurfaceVariant
        )

    LaunchedEffect(state.lines, state.tokens, colorScheme) {
        textFieldValue.value =
            textFieldValue.value.copy(annotatedString = tokenizedText(state.lines, state.tokens, colorScheme))
    }

    Box(
        Modifier.horizontalScroll(horizontalScrollState, reverseScrolling = true)
    ) {

        BasicTextField(
            value = textFieldValue.value,
            onValueChange = { new ->
                // TODO it doesn't work 🥲
//                val (value, range) = computeValueChange(textFieldValue.value, new)
//                val textLayoutResult = textLayoutResult.value
//                if (value != null && textLayoutResult != null) {
//                    val startLine = textLayoutResult.getLineForOffset(range.first)
//                    val startLineEndOffset = textLayoutResult.getLineStart(startLine)
//
//                    val start = TextChange.Pos(
//                        line = startLine,
//                        character = range.first - startLineEndOffset,
//                        globalOffset = range.first
//                    )
//                    val endLine = textLayoutResult.getLineForOffset(range.last)
//                    val endLineStartOffset = textLayoutResult.getLineStart(endLine)
//                    val end = TextChange.Pos(
//                        line = endLine,
//                        character = range.last - endLineStartOffset,
//                        globalOffset = range.last
//                    )
//                }
                onEvent(Event.OnValueChange(TextChange.ChangedCompletely(new.text)))
                textFieldValue.value = new
            },
            modifier = Modifier
                .interceptClick {
                    onEvent(Event.OnTextFieldClick(it))
                }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                        onEvent(Event.OnEnterPressed(textFieldValue.value.selection, event.isCtrlPressed))
                        return@onPreviewKeyEvent true
                    }
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace) {
                        onEvent(Event.OnBackspacePressed(textFieldValue.value.selection, event.isCtrlPressed))
                        return@onPreviewKeyEvent true
                    }
                    false
                }
                .then(
                    Modifier
                        .pointerHoverIcon(PointerIcon.Text, overrideDescendants = true)
                        .focusRequester(focusRequester)
                        .onFocusChanged { if (!it.hasFocus) onEvent(Event.OnInputFocusLost) }
                )
                .then(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(end = 50.dp)
                ),
            enabled = state.canEdit,
            readOnly = !state.canEdit,
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            onTextLayout = { textLayoutResult.value = it },
        )
        CustomCursor(
            layoutResult = textLayoutResult.value,
            textFieldValue = textFieldValue.value,
        )
    }
}


fun tokenizedText(
    lines: Lines,
    tokens: Tokens,
    scheme: TokenColorScheme,
) = buildAnnotatedString {
    // 1. Считаем смещения начала строк + собираем текст
    val lineStartOffsets = IntArray(lines.size + 1)
    var cursor = 0

    lines.forEachIndexed { index, line ->
        lineStartOffsets[index] = cursor

        append(line)
        cursor += line.length

        if (index != lines.lastIndex) {
            append('\n')
            cursor += 1
        }
    }
    // конец текста
    lineStartOffsets[lines.size] = cursor

    val textLength = cursor

    // 2. навешиваем стили по токенам
    tokens.forEach { token ->
        val lineStart = lineStartOffsets.getOrElse(token.line) { return@forEach }
        val start = (lineStart + token.offset).coerceIn(0, textLength)
        val end = (start + token.length).coerceIn(start, textLength)

        addStyle(
            style = SpanStyle(color = scheme[token.type]),
            start = start,
            end = end
        )
    }
}

data class TokenColorScheme(
    val keyword: Color,
    val function: Color,
    val variable: Color,
    val comment: Color,
    val default: Color
) {
    operator fun get(tokenType: TokenType) =
        when (tokenType) {
            TokenType.Keyword -> keyword
            TokenType.Function -> function
            TokenType.Comment -> comment
            TokenType.Variable -> variable
            TokenType.Unknown -> default
        }
}

fun computeValueChange(
    old: TextFieldValue,
    new: TextFieldValue
): Pair<String?, IntRange> {
    val oldText = old.text
    val newText = new.text

    if (oldText == newText) {
        val pos = old.selection.start.coerceIn(0, oldText.length)
        return null to (pos .. pos)
    }

    val oldSel = old.selection
    val newSel = new.selection
    val oldLen = oldText.length
    val newLen = newText.length
    val deltaLen = newLen - oldLen

    return when {
        // cursor insertion
        oldSel.collapsed && newSel.collapsed && deltaLen > 0 ->
            if (newSel.start > oldSel.start) {
                // insertion after (old) cursor - regular typing
                val endInNew = newSel.start
                val startInNew = (endInNew - deltaLen).coerceAtLeast(0)
                val inserted = newText.substring(startInNew, endInNew)
                inserted to (oldSel.start .. oldSel.start)
            } else {
                // insertion before (old) cursor - example: enter + ctrl
                val endInNew = newSel.start + deltaLen
                val startInNew = newSel.start
                val inserted = newText.substring(startInNew, endInNew)
                inserted to (oldSel.start .. oldSel.start)
            }

        // cursor deletion
        oldSel.collapsed && newSel.collapsed && deltaLen < 0 ->
            if (newSel.start < oldSel.start) {
                // deletion before (old) cursor
                "" to (oldSel.min - abs(deltaLen) .. oldSel.max)
            } else {
                // deletion after (old) cursor
                val startInOld = oldSel.min
                val endInOld = startInOld + abs(deltaLen)
                "" to (startInOld .. endInOld)
            }

        // selection replaced with insertion (paste, typing over selection) or somehow with selection
        !oldSel.collapsed -> {
            val oldStart = oldSel.min
            val oldEnd = oldSel.max
            val insertedLen = oldSel.length + deltaLen
            val inserted = newText.substring(newSel.max - insertedLen, newSel.max)
            inserted to (oldStart .. oldEnd)
        }

        else -> textChangedCompletely(old, new)
    }
}

fun textChangedCompletely(
    old: TextFieldValue,
    new: TextFieldValue
) = new.text to (0 .. old.text.length)
