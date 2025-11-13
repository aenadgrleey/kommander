package com.aenadgrleey.kommander.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.core.compose.remember
import com.aenadgrleey.kommander.core.navigation.PanesScope
import com.aenadgrleey.kommander.core.navigation.component
import com.aenadgrleey.kommander.editor.model.NavigateToCode
import com.aenadgrleey.kommander.editor.model.NavigateToNearestCode
import com.aenadgrleey.kommander.editor.model.State
import com.aenadgrleey.kommander.editor.ui.AxisLockNestedScrollConnection
import com.aenadgrleey.kommander.editor.ui.EditorBox
import com.aenadgrleey.kommander.editor.ui.EditorInput
import com.aenadgrleey.kommander.editor.utils.targetCaret
import com.aenadgrleey.kommander.editor.utils.targetX
import com.aenadgrleey.kommander.editor.utils.targetY
import com.aenadgrleey.kommander.root.AppDependencies
import com.aenadgrleey.kommander.root.model.Project
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import com.aenadgrleey.kommander.root.model.Event as RootEvent
import com.aenadgrleey.kommander.root.model.State as RootState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PanesScope.EditorPane(
    rootState: RootState,
    onRootEvent: (RootEvent) -> Unit,
    deps: AppDependencies
) {
    if (rootState.project is Project.Editable) {
        val project = remember(rootState.project) { (rootState.project as Project.Editable) }
        val editorComponent = component(
            key = project.path
        ) { EditorComponent(project.path, project.initPosition, deps.lsp, deps.fs) }

        val state by editorComponent.state.collectAsState()

        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(focusRequester) {
            delay(1.milliseconds)
            focusRequester.requestFocus()
        }

        val textLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
        val textFieldValue = remember { mutableStateOf(TextFieldValue("")) }

        val horizontalScrollState = rememberScrollState()
        val verticalScrollState = rememberScrollState()

        LaunchedEffect(editorComponent, focusRequester) {
            editorComponent.effect.collect {
                when (it) {
                    is NavigateToCode -> {
                        val textLayoutResult = textLayoutResult.value ?: return@collect
                        focusRequester.requestFocus()
                        verticalScrollState.scrollTo(textLayoutResult.targetY(it.position))
                        horizontalScrollState.animateScrollTo(textLayoutResult.targetX(it.position))
                        textFieldValue.value = textFieldValue.value.copy(
                            selection = textLayoutResult.targetCaret(it.position)
                        )
                    }

                    is NavigateToNearestCode -> {
                        val textLayoutResult = textLayoutResult.value ?: return@collect
                        horizontalScrollState.animateScrollTo(it.offset.x.toInt())
                        val caret = textLayoutResult.getOffsetForPosition(it.offset)
                        textFieldValue.value = textFieldValue.value.copy(
                            selection = TextRange(caret)
                        )
                    }
                }
            }
        }

        EditorBox(
            isHighlighted = state.canEdit,
            scrollState = verticalScrollState,
            modifier = Modifier
                .fillMaxHeight()
                .fillPanesWidth()
                .nestedScroll(AxisLockNestedScrollConnection)
                .padding(vertical = 16.dp, horizontal = 4.dp)
        ) {
            if (state is State.Ready) {
                EditorInput(
                    state = state as State.Ready,
                    onEvent = editorComponent::dispatch,
                    textLayoutResult = textLayoutResult,
                    textFieldValue = textFieldValue,
                    focusRequester = focusRequester,
                    horizontalScrollState = horizontalScrollState,
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillPanesWidth()
                        .fillMaxSize()
                ) {
                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(500)
                        isVisible = true
                    }
                    this@EditorBox.AnimatedVisibility(isVisible, enter = fadeIn(), exit = fadeOut()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 5.dp,
                        )
                    }
                }
            }

        }
    }
}
