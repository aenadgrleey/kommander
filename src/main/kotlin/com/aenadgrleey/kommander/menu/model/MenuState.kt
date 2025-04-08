package com.aenadgrleey.kommander.menu.model

data class MenuState(
    val items: List<ProjectNode>
) {
    companion object {
        val Default = MenuState(emptyList())
    }
}