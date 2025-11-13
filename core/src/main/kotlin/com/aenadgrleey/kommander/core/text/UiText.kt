package com.aenadgrleey.kommander.core.text

import org.jetbrains.compose.resources.StringResource

sealed interface UiText {
    data class Raw(val value: String) : UiText
    data class Res(val resId: StringResource, val args: List<Any>) : UiText
}