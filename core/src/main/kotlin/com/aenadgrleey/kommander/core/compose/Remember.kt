package com.aenadgrleey.kommander.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T, R> T.remember(block: (T) -> R) =
    remember(this) { block(this) }