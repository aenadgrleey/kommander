package com.aenadgrleey.kommander.lsp

import com.aenadgrleey.kommander.core.utils.logRunCatching
import com.aenadgrleey.kommander.editor.lsp.*
import com.aenadgrleey.kommander.lsp.model.Token
import com.aenadgrleey.kommander.lsp.model.toTokens
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class LspProcessor() {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()
    private val idGenerator = AtomicLong(1)

    private val process = ProcessBuilder()
        .command(KOTLIN_LSP_COMMAND)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()

    private val writer = OutputStreamWriter(process.outputStream, StandardCharsets.UTF_8)
    private val reader = BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8))

    private val events = MutableSharedFlow<LSPOutput>()

    @Volatile
    private var legend = listOf<TokenType>()

    init {
        scope.launch {
            loop@ while (true) {
                logRunCatching {
                    val headers = mutableListOf<String>()
                    while (true) {
                        val line = reader.readLine() ?: error("LSP process terminated unexpectedly")
                        if (line.isEmpty()) break
                        headers += line
                    }

                    val length =
                        headers.firstOrNull { it.startsWith("Content-Length:", ignoreCase = true) }?.substringAfter(":")
                            ?.trim()?.toIntOrNull() ?: continue@loop

                    if (length <= 0) continue@loop

                    val buf = CharArray(length)
                    var read = 0
                    while (read < length) {
                        read += reader.read(buf, read, length - read).also {
                            if (it == -1) error("LSP process terminated unexpectedly")
                        }
                    }
                    json.decodeFromString<LSPOutput>(String(buf).also { println(it) })
                }.onSuccess { events.emit(it) }
            }
        }
    }

    suspend fun initialize(path: String) = mutex.withLock {
        val id = idGenerator.getAndIncrement()
        val params = InitializeParams(
            processId = process.pid().toInt(),
            rootUri = "file://$path",
        )
        request(id, "initialize", params)
        awaitEvent<InitializeResult>(id)
            ?.capabilities
            ?.semanticTokensProvider
            ?.legend?.tokenTypes
            ?.let { legend = it }

        notify<Unit>("initialized")
    }

    suspend fun didOpen(
        path: String,
        text: String,
    ) = mutex.withLock {
        val params = DidOpenParams(
            textDocument = TextDocumentItem(
                uri = "file://$path",
                languageId = KOTLIN_LANGUAGE_ID,
                version = 1,
                text = text,
            )
        )
        notify("textDocument/didOpen", params)
    }

    suspend fun didChangeFull(
        path: String,
        version: Long,
        newText: String,
    ) = mutex.withLock {
        val params = DidChangeParams(
            textDocument = VersionedTextDocumentIdentifier(
                uri = "file://$path",
                version = version,
            ),
            contentChanges = listOf(
                TextDocumentContentChangeEvent(
                    text = newText
                )
            )
        )

        notify(
            method = "textDocument/didChange",
            params = params,
        )
    }

    suspend fun fetchSemanticTokens(
        path: String,
    ): List<Token> = mutex.withLock {
        val params = TextDocumentParams(
            textDocument = TextDocumentIdentifier(uri = "file://$path"),
        )
        val id = idGenerator.getAndIncrement()
        request(id, "textDocument/semanticTokens/full", params)

        return awaitEvent<SemanticTokensResponse>(id)
            ?.also { println(it) }
            ?.data?.toTokens(legend).orEmpty()
    }

    fun shutdown() {
        scope.launch(NonCancellable) {
            val id = idGenerator.getAndIncrement()
            request<Unit>(id, "shutdown")
            awaitEvent<Unit>(id)
            notify<Unit>("exit")

            process.waitFor(500, TimeUnit.MILLISECONDS)
            if (process.isAlive) process.destroyForcibly()
        }
    }

    private suspend inline fun <reified T> awaitEvent(id: Long) =
        events
            .first { it.id == id }.result
            ?.let { json.decodeFromJsonElement<T>(it) }
            ?.also { println(it) }

    private inline fun <reified T> request(
        id: Long,
        method: String,
        params: T? = null,
    ) = logRunCatching {
        val body = json.encodeToString(LSPInput(id = id, method = method, params = params))
        println(body)

        val bytes = body.toByteArray(StandardCharsets.UTF_8)
        val message = buildString {
            append("Content-Length: ")
            append(bytes.size)
            append("\r\n")
            append("\r\n")
            append(body)
        }

        writer.write(message)
        writer.flush()
    }

    private suspend inline fun <reified T> notify(
        method: String,
        params: T? = null,
    ) = logRunCatching {
        val body = json.encodeToString(LSPNotification(method = method, params = params))
        println(body)

        val bytes = body.toByteArray(StandardCharsets.UTF_8)
        val message = buildString {
            append("Content-Length: ")
            append(bytes.size)
            append("\r\n")
            append("\r\n")
            append(body)
        }

        writer.write(message)
        writer.flush()
    }

    companion object {
        const val KOTLIN_LANGUAGE_ID = "kotlin"
        val KOTLIN_LSP_COMMAND = listOf("kotlin-lsp", "--stdio")
    }
}