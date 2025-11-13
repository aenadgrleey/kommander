package com.aenadgrleey.kommander.editor.lsp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LSPInput<T>(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("method")
    val method: String,
    @SerialName("params")
    val params: T? = null,
    @SerialName("jsonrpc")
    val jsonrpc: String = "2.0",
)

@Serializable
data class InitializeParams(
    @SerialName("processId")
    val processId: Int,
    @SerialName("rootUri")
    val rootUri: String,
    @SerialName("capabilities")
    val capabilities: ClientCapabilities = ClientCapabilities(),
)

@Serializable
data class ClientCapabilities(
    @SerialName("textDocument")
    val textDocument: TextDocumentClientCapabilities = TextDocumentClientCapabilities(),
)

@Serializable
data class TextDocumentClientCapabilities(
    @SerialName("semanticTokens")
    val semanticTokens: SemanticTokensClientCapabilities = SemanticTokensClientCapabilities(),
)

@Serializable
data class SemanticTokensClientCapabilities(
    @SerialName("requests")
    val requests: SemanticTokensRequests = SemanticTokensRequests(),
    @SerialName("dynamicRegistration")
    val dynamicRegistration: Boolean = false,
    @SerialName("tokenTypes")
    val tokenTypes: List<String> = listOf("keyword","function","variable","string"),
    @SerialName("tokenModifiers")
    val tokenModifiers: List<String> = listOf(),
    @SerialName("formats")
    val formats: List<String> = listOf("relative"),
)

@Serializable
data class SemanticTokensRequests(
    @SerialName("full")
    val full: Boolean = true,
    @SerialName("range")
    val range: Boolean = true,
)

@Serializable
data class LSPNotification<T>(
    @SerialName("method")
    val method: String,
    @SerialName("jsonrpc")
    val jsonrpc: String = "2.0",
    @SerialName("params")
    val params: T? = null,
)

@Serializable
data class TextDocumentItem(
    @SerialName("uri")
    val uri: String,
    @SerialName("languageId")
    val languageId: String,
    @SerialName("version")
    val version: Int,
    @SerialName("text")
    val text: String,
)

@Serializable
data class DidOpenParams(
    @SerialName("textDocument")
    val textDocument: TextDocumentItem,
)

@Serializable
data class VersionedTextDocumentIdentifier(
    @SerialName("uri")
    val uri: String,
    @SerialName("version")
    val version: Long,
)

@Serializable
data class TextDocumentContentChangeEvent(
    // для full-sync range не нужен
    @SerialName("text")
    val text: String,
)

@Serializable
data class DidChangeParams(
    @SerialName("textDocument")
    val textDocument: VersionedTextDocumentIdentifier,
    @SerialName("contentChanges")
    val contentChanges: List<TextDocumentContentChangeEvent>,
)

@Serializable
data class TextDocumentIdentifier(
    val uri: String,
)

@Serializable
data class TextDocumentParams(
    val textDocument: TextDocumentIdentifier,
)

@Serializable
data class SemanticTokensResponse(
    val data: List<Int>,
    val resultId: String? = null,
)