package com.aenadgrleey.kommander.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.aenadgrleey.kommander.common.model.EditorCoordinates
import com.aenadgrleey.kommander.core.navigation.MVIComponent
import com.aenadgrleey.kommander.core.navigation.NoEvent
import com.aenadgrleey.kommander.core.utils.MainThread
import com.aenadgrleey.kommander.core.utils.isKotlinFile
import com.aenadgrleey.kommander.core.utils.safe
import com.aenadgrleey.kommander.editor.model.EditorEffect
import com.aenadgrleey.kommander.editor.model.EditorState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class EditorComponent
internal constructor(
    val initCoords: EditorCoordinates,
    val file: File,
    coroutineContext: CoroutineContext
) : MVIComponent<EditorState, NoEvent>(coroutineContext) {

    override val defaultState = EditorState.Default
    private val lastChangedAt = AtomicLong(-1L)
    private val init = CompletableDeferred(Unit)

    var value by mutableStateOf(TextFieldValue(""))
        private set

    init {
        coroutineScope.launch {
            while (isActive) {
                val ls = file.lastModified()
                if (lastChangedAt.get() < ls) {
                    val text = safe { file.readText() }
                    if (text != null) {
                        val init = this@EditorComponent.init.complete(Unit)
                        if (init) effect(EditorEffect.NavigateToCode(initCoords))

                        value = value.copy(annotate(text))
                        lastChangedAt.set(ls)
                        mState.update { it.copy(canEdit = file.isKotlinFile) }
                    } else {
                        val placeholder = "Cannot edit!"
                        value = TextFieldValue(AnnotatedString(placeholder), TextRange(placeholder.length))
                        mState.update { it.copy(canEdit = false) }
                    }
                }
                delay(10.seconds)
            }
        }
        coroutineScope.launch {
            snapshotFlow { value.text }
                .distinctUntilChanged()
                .onEach { lastChangedAt.set(System.currentTimeMillis()) }
                .debounce(10.seconds)
                .collectLatest {
                    init.await()
                    file.writeText(it)
                }
        }
    }

    private var saveJob: Job? = null
    fun forceSave() {
        saveJob?.cancel()
        saveJob = coroutineScope.launch {
            init.await()
            withContext(NonCancellable) {
                lastChangedAt.set(System.currentTimeMillis())
                file.writeText(value.text)
            }
        }
    }

    fun forceCoordinates(coordinates: EditorCoordinates) {
        effect(EditorEffect.NavigateToCode(coordinates))
    }


    private var keywordSpan = SpanStyle(
        color = Color.Red,
        fontWeight = FontWeight.Bold,
    )

    @MainThread
    fun handleValueChange(changed: TextFieldValue) {
        runCatching {

            // updating selection and composition without reannoting text
            // reannotating only changed lines
            value = if (value.text == changed.text)
                value.copy(
                    selection = changed.selection,
                    composition = changed.composition
                )
            else {
                val annotatedString = buildAnnotatedString {
                    val startAnchor = min(value.selection.min, changed.selection.min)
                    val endAnchor = max(value.selection.max, changed.selection.max)

                    var renderStart = 0
                    for (i in (0..startAnchor).reversed()) {
                        if (changed.text[i] == '\n') {
                            renderStart = i
                            break
                        }
                    }

                    var renderEnd = changed.text.length
                    for (i in endAnchor..changed.text.lastIndex) {
                        if (changed.text[i] == '\n') {
                            renderEnd = i
                            break
                        }
                    }


                    val readyPrefix = value.annotatedString.subSequence(0, renderStart)
                    append(readyPrefix)

                    append(annotate(changed.annotatedString.substring(renderStart, renderEnd)))

                    val shift = value.text.length - changed.text.length
                    val readySuffix = value.annotatedString.subSequence(renderEnd + shift, value.text.length)
                    append(readySuffix)
                }
                changed.copy(annotatedString)
            }
        }.onFailure {
            // that's called a "too lazy for tests" and rush before deadlines :))))
            value = changed.copy(annotatedString = annotate(changed.text))
        }
    }

    private fun annotate(text: String) = buildAnnotatedString {
        append(text)
        separatorRegex.findAll(text).forEach { match ->
            addStyle(keywordSpan, match.range.first, match.range.last + 1)
        }
    }

    @MainThread
    fun bindHighlightColor(color: Color) {
        keywordSpan = keywordSpan.copy(color = color)
        value = value.copy(annotate(value.text))
    }


    override fun dispatch(event: NoEvent) = Unit


    companion object {

        private val kotlinKeywords = setOf(
            // Hard keywords (reserved and cannot be used as identifiers)
            "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
            "interface", "is", "null", "object", "package", "return", "super", "this", "throw",
            "true", "try", "typealias", "typeof", "val", "var", "when", "while",

            // Soft keywords (used in specific contexts)
            "by", "catch", "constructor", "delegate", "dynamic", "field", "file", "finally",
            "get", "import", "init", "param", "property", "receiver", "set", "setparam",
            "where", "actual", "abstract", "annotation", "companion", "const", "crossinline",
            "data", "enum", "expect", "external", "final", "infix", "inline", "inner", "internal",
            "lateinit", "noinline", "open", "operator", "out", "override", "private", "protected",
            "public", "reified", "sealed", "suspend", "tailrec", "vararg", "value", "field", "context",
        )

        private val separatorRegex =
            Regex("""(?<!\w)(?:${kotlinKeywords.joinToString("|") { Regex.escape(it) }})(?!\w)""")
    }

}