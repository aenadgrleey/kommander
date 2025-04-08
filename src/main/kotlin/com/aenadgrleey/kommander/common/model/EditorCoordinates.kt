package com.aenadgrleey.kommander.common.model

data class EditorCoordinates(
    val line: Int,
    val column: Int,
) {
    companion object {
        val Default = EditorCoordinates(
            line = 0,
            column = 0,
        )
    }
}
