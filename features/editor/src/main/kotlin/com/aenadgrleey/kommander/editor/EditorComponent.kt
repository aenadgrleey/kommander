package com.aenadgrleey.kommander.editor

import com.aenadgrleey.kommander.core.navigation.MVIComponent
import com.aenadgrleey.kommander.core.text.TextPosition
import com.aenadgrleey.kommander.core.text.isDefined
import com.aenadgrleey.kommander.core.utils.isKotlinFile
import com.aenadgrleey.kommander.editor.model.*
import com.aenadgrleey.kommander.editor.utils.splitLines
import com.aenadgrleey.kommander.lsp.LspProcessor
import com.aenadgrleey.kommander.lsp.model.TextChange
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.FileSystem
import okio.Path
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class EditorComponent
internal constructor(
    val path: Path,
    initPosition: TextPosition,
    val lspProcessor: LspProcessor,
    val fileSystem: FileSystem,
) : MVIComponent<State, Event>() {

    private val mutex = Mutex()
    private val version = AtomicLong(0L)
    private val shouldAnnotate = path.isKotlinFile
    override val defaultState = State.Init(canEdit = path.isKotlinFile)
    private val lastChangedAt = AtomicLong(-1L)

    init {
        coroutineScope.launch {
            while (isActive) {
                val metadata = fileSystem.metadataOrNull(path)
                val ls = metadata?.lastModifiedAtMillis ?: -1
                if (ls > lastChangedAt.get()) {
                    runCatching {
                        fileSystem.read(path) { splitLines(readUtf8()) }
                    }.onSuccess { lines ->
                        if (lastChangedAt.get() == -1L && initPosition.isDefined) {
                            effect(NavigateToCode(initPosition))
                        }
                        if (lastChangedAt.get() == -1L) {
                            val tokens =
                                if (!shouldAnnotate) emptyList()
                                else {
                                    lspProcessor.didOpen(path.toString(), lines.joinToString("\n"))
                                    delay(5000)
                                    lspProcessor.fetchSemanticTokens(path.toString())
                                }
                            mState.update { State.Ready(lines, tokens, it.canEdit) }
                        }
                        lastChangedAt.set(ls)
                    }
                }
                delay(10.seconds)
            }
        }
        coroutineScope.launch {
            state
                .onEach { lastChangedAt.set(System.currentTimeMillis()) }
                .debounce(10.seconds)
                .collectLatest {
                    if (it !is State.Ready) return@collectLatest
                    mutex.withLock {
                        fileSystem.write(path) {
                            for (line in it.lines) {
                                writeUtf8(line)
                                writeUtf8("\n")
                            }
                        }
                    }
                }
        }
    }

    override fun dispatch(event: Event) {
        when (event) {
            is Event.OnTextFieldClick -> effect(NavigateToNearestCode(event.offset))
            Event.OnInputFocusLost -> forceSave()
            is Event.OnValueChange -> when (event.textChange) {
                is TextChange.ChangedCompletely -> {
                    val newLines = splitLines(event.textChange.text)
                    mState.update {
                        if (it !is State.Ready) it
                        else it.copy(lines = Lines(newLines))
                    }
                    coroutineScope.launch {
                        lspProcessor.didChangeFull(path.toString(), version.incrementAndGet(), event.textChange.text)
                        val newTokens = lspProcessor.fetchSemanticTokens(path.toString())
                        mState.update {
                            if (it !is State.Ready) it
                            else it.copy(tokens = Tokens(newTokens))
                        }
                    }
                }

                is TextChange.Ranged -> TODO()
            }

            is Event.OnBackspacePressed -> TODO()
            is Event.OnEnterPressed -> TODO()
        }
    }

    private var saveJob: Job? = null
    fun forceSave() {
        saveJob?.cancel()
        saveJob = coroutineScope.launch {
//            val text = value.text
//            withContext(NonCancellable) {
//                mutex.withLock {
//                    lastChangedAt.set(System.currentTimeMillis())
//                    fileSystem.write(path) { writeUtf8(text) }
//                }
//            }
        }
    }

    override fun onClear() {
        forceSave()
    }

}