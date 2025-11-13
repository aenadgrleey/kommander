package com.aenadgrleey.kommander.lsp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

sealed interface TextChange {

    @Serializable
    data class Ranged(
        @SerialName("range")
        val range: Range,
        @SerialName("text")
        val text: String,
        @SerialName("rangeLength")
        val rangeLength: Int
    ) : TextChange

    data class ChangedCompletely(
        @SerialName("text")
        val text: String,
    ) : TextChange
}

@Serializable
data class Range(
    @SerialName("start")
    val start: Pos,
    @SerialName("end")
    val end: Pos
)

@Serializable
data class Pos(
    @SerialName("line")
    val line: Int,
    @SerialName("character")
    val character: Int,
    @Transient
    val globalOffset: Int = -1
)