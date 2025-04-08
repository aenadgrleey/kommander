package com.aenadgrleey.kommander.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.TextFieldScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.core.compose.remember
import com.aenadgrleey.kommander.core.navigation.PanesScope
import com.aenadgrleey.kommander.editor.model.EditorEffect
import com.aenadgrleey.kommander.editor.ui.EditorBox
import com.aenadgrleey.kommander.editor.ui.LineNumbers

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PanesScope.EditorPane(
    editorComponent: EditorComponent
) {
    val state by editorComponent.state.collectAsState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val focusRequester = remember { FocusRequester() }

    val scriptScrollState = remember { TextFieldScrollState(Orientation.Vertical) }
    val horizontalScrollState = rememberScrollState()


    LaunchedEffect(Unit) {
        editorComponent.effect.collect {
            when (it) {
                is EditorEffect.NavigateToCode -> {

                    focusRequester.requestFocus()

                    val targetY = textLayoutResult
                        ?.getLineTop((it.coordinates.line - 1).coerceAtLeast(0))
                        ?.toInt() ?: return@collect

                    scriptScrollState.offset = targetY.toFloat()

                    val targetCaret = textLayoutResult
                        ?.getLineStart((it.coordinates.line - 1).coerceAtLeast(0))
                        ?: return@collect
                    editorComponent.handleValueChange(
                        editorComponent.value.copy(selection = TextRange(targetCaret + it.coordinates.column))
                    )
                }
            }
        }
    }


    val highlight = MaterialTheme.colorScheme.primary
    LaunchedEffect(highlight, editorComponent) {
        editorComponent.bindHighlightColor(highlight)
    }

    EditorBox(
        isHighlighted = state.canEdit,
        modifier = Modifier
            .fillMaxHeight()
            .fillPanesWidth()
            .padding(vertical = 16.dp, horizontal = 4.dp)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(8.dp)
        )
        val textStyle = MaterialTheme.typography.bodyLarge
            .remember {
                it.copy(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Bottom,
                        trim = LineHeightStyle.Trim.None
                    )
                )
            }
        LineNumbers(
            lineCount = textLayoutResult?.lineCount ?: 0,
            textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            scrollState = scriptScrollState,
        )
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp)
        )

        BasicTextField(
            value = editorComponent.value,
            onValueChange = {
                editorComponent.handleValueChange(it)
                it.selection
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { if (!it.hasFocus) editorComponent.forceSave() }
                .fillMaxHeight()
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
                .padding(vertical = 4.dp),
            enabled = state.canEdit,
            readOnly = !state.canEdit,
            textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurface),
            onTextLayout = { textLayoutResult = it },
            scrollState = scriptScrollState

        )
    }
}
