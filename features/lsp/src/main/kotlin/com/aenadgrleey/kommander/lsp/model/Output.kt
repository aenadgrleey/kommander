package com.aenadgrleey.kommander.editor.lsp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class LSPOutput(
    @SerialName("result")
    val result: JsonElement? = null,
    @SerialName("id")
    val id: Long? = null,
)

@Serializable
data class SemanticsResult(
    @SerialName("data")
    val data: List<Int> = emptyList(),
    @SerialName("resultId")
    val resultId: String = "",
)


@Serializable
data class InitializeResult(
    @SerialName("capabilities")
    val capabilities: ServerCapabilities,
    @SerialName("serverInfo")
    val serverInfo: ServerInfo? = null,
)

@Serializable
data class ServerInfo(
    @SerialName("name")
    val name: String,
    @SerialName("version")
    val version: String? = null,
)

@Serializable
data class ServerCapabilities(
    @SerialName("semanticTokensProvider")
    val semanticTokensProvider: SemanticsTokensProvider? = null,
)

@Serializable
data class SemanticsTokensProvider(
    @SerialName("legend")
    val legend: Legend,
    @SerialName("full")
    val full: Boolean,
    @SerialName("range")
    val range: Boolean,
)

@Serializable
data class Legend(
    @SerialName("tokenTypes")
    val tokenTypes: List<TokenType>,
    @SerialName("tokenModifiers")
    val tokenModifiers: List<String> = emptyList(),
)