package com.aenadgrleey.kommander.app_actions.new_script.model

sealed interface State {
    data object NotVisible : State
    data class Visible(
        val dirPath: String,
        val name: String = "file.kts",
        val fileError: String? = null,
        val dirError: String? = null
    ) : State

    companion object {
        fun Init(
            dirPath: String = System.getProperty("user.dir"),
        ) = Visible(
            dirPath = dirPath,
            name = "file.kts",
            fileError = null,
            dirError = null
        )
    }
}