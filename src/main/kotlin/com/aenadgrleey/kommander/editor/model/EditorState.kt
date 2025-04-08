package com.aenadgrleey.kommander.editor.model


data class EditorState(
    val canEdit: Boolean,
) {

    companion object {
        val Default = EditorState(canEdit = true)
    }
}