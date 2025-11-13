package com.aenadgrleey.kommander.editor.lsp

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TokenTypeSerializer::class)
enum class TokenType {
    @SerialName("keyword")
    Keyword,

    @SerialName("function")
    Function,

    @SerialName("variable")
    Variable,

    @SerialName("string")
    Comment,


    Unknown
}

private object TokenTypeSerializer : KSerializer<TokenType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TokenType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TokenType) {
        val name = when (value) {
            TokenType.Keyword  -> "keyword"
            TokenType.Function -> "function"
            TokenType.Variable -> "variable"
            TokenType.Comment  -> "string"
            TokenType.Unknown  -> "unknown"
        }
        encoder.encodeString(name)
    }

    override fun deserialize(decoder: Decoder): TokenType {
        return when (val s = decoder.decodeString()) {
            "keyword"  -> TokenType.Keyword
            "function" -> TokenType.Function
            "variable" -> TokenType.Variable
            "string"   -> TokenType.Comment
            else       -> TokenType.Unknown
        }
    }
}